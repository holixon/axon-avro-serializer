package io.holixon.axon.avro.serializer.converter.specificrecord

import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class SpecificRecordConverterTest {

  private val registry = AxonAvroSchemaApi.InMemoryAxonAvroSchemaRegistry.createDefault()

  private val encoder = SpecificRecordToByteArrayConverter(registry)
  private val decoder = ByteArrayToSpecificRecordConverter(registry)

  private val sample = SampleEvent("foo")

  @Test
  internal fun `convert back and forth`() {
    val bytes = encoder.convert(sample)

    val record = decoder.convert(bytes)

    assertThat(record).isEqualTo(sample)
  }
}
