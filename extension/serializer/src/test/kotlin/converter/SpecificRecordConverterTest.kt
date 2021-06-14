package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class SpecificRecordConverterTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  private val converter = SpecificRecordAxonConverter(registry)

  private val sample = SampleEvent("foo")


  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll())
  }

  @Test
  internal fun `convert back and forth`() {
    registry.register(SampleEvent.getClassSchema())

    val genericRecord = converter.toGenericDataRecord(sample)

    val decoded = converter.fromGenericDataRecord(genericRecord)

    assertThat(decoded).isEqualTo(sample)
  }
}
