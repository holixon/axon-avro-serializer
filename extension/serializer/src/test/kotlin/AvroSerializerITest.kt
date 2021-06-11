package io.holixon.axon.avro.serializer

import bankaccount.BankAccount
import bankaccount.command.CreateBankAccount
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.projection.CurrentBalanceProjection.CurrentBalance
import bankaccount.query.CurrentBalanceQueries.CurrentBalanceQuery
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.config.Configuration
import org.axonframework.config.Configurer
import org.axonframework.config.DefaultConfigurer
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.TrackedEventMessage
import org.axonframework.eventhandling.TrackingToken
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.serialization.Converter
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.Serializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent
import java.util.concurrent.ConcurrentHashMap

@Disabled
class AvroSerializerITest {


  /**
   * Configure event match history projection.
   */
  private fun EventProcessingConfigurer.registerEventHandler(projection: CurrentBalanceProjection) {
    // event handler
    // in-mem room history projection
    val processor = "current-balance-in-mem-processor"
    this.registerEventHandler { projection }
    this.registerTokenStore(processor) { InMemoryTokenStore() }
    this.assignProcessingGroup("current-balance-projection", processor)
  }


  private val eventStore = InMemoryEventStorageEngine()
  private val projectionMap = ConcurrentHashMap<String, Int>()
  private val projection = CurrentBalanceProjection(projectionMap)
  private val avroSerializer = AvroSerializer.defaultSerializer()


  val brokenSerializer = object : Serializer {
    override fun <T : Any?> serialize(p0: Any?, p1: Class<T>?): SerializedObject<T> {
      TODO("Not yet implemented")
    }

    override fun <T : Any?> canSerializeTo(p0: Class<T>?): Boolean {
      TODO("Not yet implemented")
    }

    override fun <S : Any?, T : Any?> deserialize(p0: SerializedObject<S>?): T {
      TODO("Not yet implemented")
    }

    override fun classForType(p0: SerializedType?): Class<*> {
      TODO("Not yet implemented")
    }

    override fun typeForClass(p0: Class<*>?): SerializedType {
      TODO("Not yet implemented")
    }

    override fun getConverter(): Converter {
      TODO("Not yet implemented")
    }
  }

  private val configurer: Configurer = DefaultConfigurer
    .defaultConfiguration()
    .configureEmbeddedEventStore { eventStore }
    .configureCommandBus { SimpleCommandBus.builder().build() }
    //.configureQueryBus { SimpleQueryBus.builder().build() }
    .configureAggregate(BankAccount::class.java)
    //.registerQueryHandler { projection }
    .eventProcessing { it.registerEventHandler(projection) }
    .configureSerializer { brokenSerializer }
    .configureMessageSerializer { brokenSerializer }
    .configureEventSerializer { brokenSerializer }

  lateinit var axonConfiguration : Configuration

  private val commandGateway by lazy {
    axonConfiguration.commandGateway()
  }

  private val queryGateway by lazy {
    axonConfiguration.queryGateway()
  }

  @BeforeEach
  internal fun setUp() {
    axonConfiguration = configurer.start()

  }

  @AfterEach
  internal fun tearDown() {
    if (this::axonConfiguration.isInitialized) {
      this.axonConfiguration.shutdown()
    }
  }

  @Test
  internal fun `just create account`() {

    assertThat(projectionMap).isEmpty()
    commandGateway.sendAndWait<String>(CreateBankAccount("1", 100))

    await.untilAsserted { assertThat(projectionMap).isNotEmpty }
  }

  @Test
  internal fun `create account`() {

    println("event: ${SampleEvent.newBuilder().setValue("foo").build()}")

    assertThat(projectionMap).isEmpty()
    commandGateway.sendAndWait<String>(CreateBankAccount("1", 100))

    await.untilAsserted { assertThat(projectionMap).isNotEmpty }

    val balance = queryGateway.query(CurrentBalanceQuery("1")).join()

    assertThat(balance).hasValue(CurrentBalance("1",100))

    val map = eventStore.map

    println(map)
  }

  private fun QueryGateway.query(query: CurrentBalanceQuery) = this.query(query, ResponseTypes.optionalInstanceOf(CurrentBalance::class.java))

  @Suppress("UNCHECKED_CAST")
  private val InMemoryEventStorageEngine.map : Map<TrackingToken, TrackedEventMessage<*>> get() = InMemoryEventStorageEngine::class.java.getDeclaredField("events")
    .let {
      it.isAccessible = true
      return@let it.get(this) as Map<TrackingToken, TrackedEventMessage<*>>
    }
}
