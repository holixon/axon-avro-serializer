package io.holixon.axon.avro.schema.api

import io.holixon.axon.avro.lib.test.AxonAvroSerializerTestLib
import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi.AxonAvroSchemaData
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AxonAvroSchemaTest {

  private val sampleEventSchema = AxonAvroSerializerTestLib.loadArvoResource("test.fixture.SampleEvent-v4711")

  @Test
  internal fun `read schema and derive values`() {
    val schema = Schema.Parser().parse(sampleEventSchema)

    val axonSchema = AxonAvroSchemaData(globalId = 1, schema = schema)

    assertThat(axonSchema.globalId).isEqualTo(1L)
    assertThat(axonSchema.revision).isEqualTo("4711")
    assertThat(axonSchema.name).isEqualTo("SampleEvent")
    assertThat(axonSchema.context).isEqualTo("test.fixture")
    assertThat(axonSchema.canonicalName).isEqualTo("test.fixture.SampleEvent")

  }
}

