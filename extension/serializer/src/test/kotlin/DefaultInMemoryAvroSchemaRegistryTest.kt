package io.holixon.axon.avro.serializer


import io.holixon.axon.avro.common.ext.SchemaExt.fingerprint
import io.toolisticon.avro.adapter.common.AvroAdapterDefault
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class DefaultInMemoryAvroSchemaRegistryTest {

  private val defaultRegistry = AvroAdapterDefault.inMemorySchemaRepository()

  private val schema = SampleEvent.getClassSchema()
  private val schemaId = schema.fingerprint
  private val schemaWithId = AxonAvroExtension.avroSchema(schemaId, schema)

  @BeforeEach
  internal fun setUp() {
    assertThat(defaultRegistry.findAll()).isEmpty()
  }

  @Test
  internal fun `register returns schemaWithId`() {
    val registered = defaultRegistry.register(schema)

    assertThat(defaultRegistry.findAll()).hasSize(1)

    assertThat(registered.id).isEqualTo(schemaId)
    assertThat(registered.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(registered.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(registered.revision).isEqualTo("4711")
  }


  @Test
  internal fun `can register schema and find by id`() {
    defaultRegistry.register(SampleEvent.getClassSchema())
    assertThat(defaultRegistry.findAll()).isNotEmpty
    assertThat(defaultRegistry.findById(schemaId)).hasValue(schemaWithId)
  }
}
