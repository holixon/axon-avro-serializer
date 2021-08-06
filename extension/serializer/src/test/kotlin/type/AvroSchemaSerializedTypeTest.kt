package io.holixon.axon.avro.serializer.type

import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.SimpleSerializedType
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSchemaSerializedTypeTest {
  companion object : KLogging()

  @Test
  internal fun `create with sample event 4711`() {
    val type = AvroSchemaSerializedType(SampleEvent.getClassSchema())

    assertThat(type.name).isEqualTo("test.fixture.SampleEvent")
    assertThat(type.revision).isEqualTo("4711")
    assertThat(type.schemaWithId.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(type.schemaWithId.schemaId).isEqualTo("-6549126288393660212")
    logger.info { "type: $type" }

  }

  @Test
  internal fun `equal and hashcode based on simpleSerializedType`() {
    val simple = SimpleSerializedType("test.fixture.SampleEvent", "4711")
    val type = AvroSchemaSerializedType(SampleEvent.getClassSchema())

    assertThat(simple.name).isEqualTo(type.name)
    assertThat(simple.revision).isEqualTo(type.revision)

    assertThat(type).isEqualTo(simple)
    assertThat(type.hashCode()).isEqualTo(simple.hashCode())
    assertThat(simple.hashCode()).isEqualTo(type.hashCode())
    assertThat(simple).isEqualTo(type.simpleSerializedType)
  }
}
