package io.holixon.axon.avro.serializer.metadata

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.messaging.MetaData
import org.junit.jupiter.api.Test

internal class AvroMetaDataExtTest {

  @Test
  internal fun `avro meta to axon meta`() {
    val avro = AvroMetaData.newBuilder()
      .setValues(
        mapOf(
          "foo" to "bar"
        )
      ).build()

    assertThat(AvroMetaDataExt.convertFromAvro(avro)["foo"]).isEqualTo("bar")
  }

  @Test
  internal fun `axon meta to avro meta`() {
    val axon = MetaData.with("foo", "bar")

    assertThat(AvroMetaDataExt.convertToAvro(axon).values["foo"]).isEqualTo("bar")
  }

  @Test
  internal fun `metaData registry contains single entry`() {
    assertThat(AvroMetaDataExt.metaDataSchemaRegistry.findAll()).hasSize(1)
    assertThat(AvroMetaDataExt.metaDataSchemaRegistry.schemaResolver()
      .apply(AvroMetaDataExt.SCHEMA_WITH_ID.schemaId)
      .map { it.schema }
    ).hasValue(AvroMetaDataExt.METADATA_SCHEMA)
  }
}
