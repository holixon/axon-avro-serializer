package io.holixon.axon.avro.serializer

import bankaccount.BankAccount
import bankaccount.command.CreateBankAccount
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.projection.CurrentBalanceProjection.CurrentBalance
import bankaccount.projection.CurrentBalanceProjection.Queries.CurrentBalanceQuery
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
import org.axonframework.queryhandling.SimpleQueryBus
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class AvroSerializerITest {


  /**
   * Configure event match history projection.
   */
  fun EventProcessingConfigurer.registerEventHandler(projection: CurrentBalanceProjection) {
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
  private val jacksonSerializer = JacksonSerializer.defaultSerializer()
  private val avroSerializer = AvroSerializer.defaultSerializer()


  private val configurer: Configurer = DefaultConfigurer
    .defaultConfiguration()
    .configureEmbeddedEventStore { eventStore }
    .configureCommandBus { SimpleCommandBus.builder().build() }
    .configureQueryBus { SimpleQueryBus.builder().build() }
    .configureAggregate(BankAccount::class.java)
    .registerQueryHandler { projection }
    .eventProcessing { it.registerEventHandler(projection) }
    .configureSerializer { jacksonSerializer }
    .configureMessageSerializer { avroSerializer }
    .configureEventSerializer { avroSerializer }

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
  internal fun `create account`() {
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
