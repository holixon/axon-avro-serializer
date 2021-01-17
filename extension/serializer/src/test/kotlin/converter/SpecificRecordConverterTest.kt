package io.holixon.axon.avro.serializer.converter

import io.holixon.axon.avro.common.ext.ByteArrayExt.toHexString
import io.holixon.axon.avro.common.ext.SchemaExt.fingerprint
import io.holixon.axon.avro.serializer.AxonAvroExtension
import io.holixon.axon.avro.serializer.AxonAvroExtension.avroSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class SpecificRecordConverterTest {

  private val registry = AxonAvroExtension.defaultInMemorySchemaRegistry()

  private val converter = SpecificRecordConverter(registry)

  private val sample = SampleEvent("foo")

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll())
  }

  @Test
  internal fun `can register schema and find by id`() {
    registry.register(SampleEvent.getClassSchema())
    assertThat(registry.findAll()).isNotEmpty
    assertThat(registry.findById(sample.schema.fingerprint)).hasValue(avroSchema(sample.schema.fingerprint, sample.schema))
  }

  @Test
  internal fun `convert back and forth`() {
    registry.register(SampleEvent.getClassSchema())

    println(sample.schema)
    val bytes = converter.toByteArray(sample)
    println(bytes.toHexString())

    val decoded = converter.fromByteArray(bytes)

    assertThat(decoded).isEqualTo(sample)

    println(decoded)
  }
}
