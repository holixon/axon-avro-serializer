package io.holixon.axon.avro.common.type

import io.holixon.axon.avro.common.AvroCommon
import io.holixon.axon.avro.common.AvroCommonTest
import io.holixon.axon.avro.common.ext.FunctionExt.invoke
import io.holixon.axon.avro.lib.test.AxonAvroSerializerTestLib
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSchemaWithIdTest {

  private val sampleEventSchema = AxonAvroSerializerTestLib.loadArvoResource("test.fixture.SampleEvent-v4711")

  @Test
  internal fun `read schema and derive values`() {
    val schema = Schema.Parser().parse(sampleEventSchema)

    val axonSchema = AvroCommon.AvroSchemaWithIdData(id = 1L, schema = schema, revision = AvroCommonTest.revisionResolver.invoke(schema).orElse(null))

    assertThat(axonSchema.id).isEqualTo(1L)
    assertThat(axonSchema.revision).isEqualTo("4711")
    assertThat(axonSchema.name).isEqualTo("SampleEvent")
    assertThat(axonSchema.context).isEqualTo("test.fixture")
    assertThat(axonSchema.canonicalName).isEqualTo("test.fixture.SampleEvent")
  }
}
