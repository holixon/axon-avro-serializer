package io.holixon.axon.avro.schema.api

import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi.InMemoryAxonAvroSchemaRegistry
import io.holixon.axon.avro.schema.api.ext.SchemaExt.info
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class InMemoryAxonAvroSchemaRegistryTest {
  val registry = InMemoryAxonAvroSchemaRegistry.createDefault()

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.store).isEmpty()
  }

  @Test
  internal fun `counting ids`() {
    assertThat(registry.idGenerator()).isEqualTo(1L)
    assertThat(registry.idGenerator()).isEqualTo(2L)
  }

  @Test
  internal fun `register non existing`() {
    val schema = registry.register(SampleEvent.getClassSchema())

    assertThat(schema.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(schema.globalId).isEqualTo(1L)
  }

  @Test
  internal fun `register twice returns existing`() {
    var schema = registry.register(SampleEvent.getClassSchema())
    schema = registry.register(schema = schema.schema)

    assertThat(schema.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(schema.globalId).isEqualTo(1L)
  }

  @Test
  internal fun `fid by info`() {
    val schema = SampleEvent.getClassSchema()
    assertThat(registry.findByInfo(schema.info))
      .isEmpty

    registry.register(schema = schema)

    assertThat(registry.findByInfo(schema.info))
      .isNotEmpty
  }
}
