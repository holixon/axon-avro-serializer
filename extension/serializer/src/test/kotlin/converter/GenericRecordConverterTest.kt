package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class GenericRecordConverterTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  private val toBytes = GenericDataRecordToAvroSingleObjectEncodedConverter()
  private val fromBytes = AvroSingleObjectEncodedToGenericDataRecordTypeConverter(registry.schemaResolver())

  private val sample = GenericData.Record(SampleEvent.`SCHEMA$`).apply {
    put("value", "foo")
  }

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll())
  }

  @Test
  internal fun `convert back and forth`() {
    registry.register(SampleEvent.getClassSchema())

    val bytes = toBytes.convert(sample)

    val decoded = fromBytes.convert(bytes)

    assertThat(decoded).isEqualTo(sample)
  }
}
