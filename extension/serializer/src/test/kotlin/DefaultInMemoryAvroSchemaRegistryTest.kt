package io.holixon.axon.avro.serializer


import io.holixon.axon.avro.common.ext.SchemaExt.fingerprint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class DefaultInMemoryAvroSchemaRegistryTest {

  private val defaultRegistry = AxonAvroExtension.defaultInMemorySchemaRegistry()

  @BeforeEach
  internal fun setUp() {
    assertThat(defaultRegistry.findAll()).isEmpty()
  }

  @Test
  internal fun `register returns schemaWithId`() {
    val registered = defaultRegistry.register(SampleEvent.getClassSchema())

    assertThat(defaultRegistry.findAll()).hasSize(1)

    assertThat(registered.id).isEqualTo(SampleEvent.getClassSchema().fingerprint)
    assertThat(registered.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(registered.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(registered.revision).isEqualTo("4711")
  }

}
