@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package io.holixon.axon.avro.serializer.spring.itest.upcaster

import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaRevision
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerConfiguration
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerSpringBase.PROFILE_ITEST
import io.holixon.axon.avro.serializer.spring.container.AxonServerContainer
import mu.KLogging
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import upcaster.itest.DummyEvent
import java.util.function.Function


class Projection {
  companion object : KLogging()

  val events = mutableListOf<DummyEvent>()

  @EventHandler
  fun on(event: DummyEvent) {
    events.add(event)

    logger.info { "received: $event" }
  }

}


@SpringBootTest(classes = [AxonAvroUpcasterITest.Companion.TestApp::class], webEnvironment = RANDOM_PORT)
@Testcontainers
@ActiveProfiles(PROFILE_ITEST)
internal class AxonAvroUpcasterITest {
  companion object : KLogging() {
    @Container
    val axon = AxonServerContainer()

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)

    @SpringBootApplication
    @Import(AxonAvroSerializerConfiguration::class)
    @Profile(PROFILE_ITEST)
    class TestApp {

      @Bean
      fun projection() = Projection()

      @Bean
      fun schemaRegistry() = DummyEvents.registry

      @Bean
      fun dummyEventUpcaster() = object : SingleEventUpcaster() {
        override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
          return intermediateRepresentation.type.name == DummyEvents.SCHEMA_EVENT_01.fullName && intermediateRepresentation.type.revision == DummyEvents.SCHEMA_EVENT_01.avroSchemaRevision
        }

        override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
          return intermediateRepresentation.upcast(
            SimpleSerializedType(DummyEvents.SCHEMA_EVENT_10.fullName, DummyEvents.SCHEMA_EVENT_10.avroSchemaRevision),
            GenericData.Record::class.java,
            // THIS throws "Not a valid schema field: value10" Function { it.apply { put("value10", "bar") } },
            Function {
              GenericData.Record(DummyEvents.SCHEMA_EVENT_10).apply {
                put("value01", it.get("value01"))
                put("value10", "bar")
              }
            },
            Function.identity()
          )
        }
      }
    }
  }

  @Autowired
  lateinit var eventGateway: EventGateway

  @Autowired
  lateinit var projection: Projection

  @Test
  internal fun `upcast from 01 to 10 by adding value10`() {
    val event01 = DummyEvents.SCHEMA_EVENT_01.createGenericRecord {
      put("value01", "foo")
    }

    //eventGateway.publish(DummyEvent.newBuilder().setValue01("foo").setValue10("bar").build())
    eventGateway.publish(event01)

    await.untilAsserted { assertThat(projection.events).isNotEmpty }
  }

}
