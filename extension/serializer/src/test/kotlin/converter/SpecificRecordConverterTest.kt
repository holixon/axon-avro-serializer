package io.holixon.axon.avro.serializer.converter

import io.holixon.axon.avro.serializer.AxonAvroExtension
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
  internal fun `convert back and forth`() {
    registry.register(SampleEvent.getClassSchema())

    val bytes = converter.toByteArray(sample)

    val decoded = converter.fromByteArray(bytes)

    assertThat(decoded).isEqualTo(sample)
  }
}
