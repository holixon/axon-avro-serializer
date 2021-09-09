package io.holixon.axon.avro.serializer.metadata

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.messaging.MetaData
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate

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

  @Test
  internal fun `can convert all primitive types that axon supports with grpc`() {
//    * Convert the given {@code value} into a {@link MetaDataValue}, attempting to maintain the source type as much as
//    * possible in the returned {@link MetaDataValue}.
//    * <ul>
//    * <li>A CharSequence (such as String) is stored as a 'string'</li>
//    * <li>A Float or Double values is represented as a 'double'</li>
//    * <li>A Number that is not a Double or Float is represented as a 'sint64'</li>
//    * <li>A Boolean is represented as a 'bool'</li>
//    * <li>Any other object is serialized and stored as bytes</li>
//    * </ul>
    val now = LocalDate.now()
    val axon = MetaData.from(mapOf(
      "string" to "hello World",
      "float" to 1.0F,
      "double" to 1.0,
      "int" to 1,
      "integer" to Integer.getInteger("1"),
      "long" to 1L,
      "boolean" to true,
      "null" to null
    ))

    var avro: AvroMetaData = AvroMetaDataExt.convertToAvro(axon)
    // serialize/deserialize
    val buffer = avro.toByteBuffer()
    avro = AvroMetaData.fromByteBuffer(buffer)

    // check values
    assertThat(avro.values["string"]).isEqualTo("hello World")
    assertThat(avro.values["float"]).isEqualTo(1.0F)
    assertThat(avro.values["double"]).isEqualTo(1.0)
    assertThat(avro.values["int"]).isEqualTo(1)
    assertThat(avro.values["integer"]).isEqualTo(Integer.getInteger("1"))
    assertThat(avro.values["long"]).isEqualTo(1L)
    assertThat(avro.values["boolean"]).isEqualTo(true)
    assertThat(avro.values["null"]).isEqualTo(null)
  }

  @Test
  @Disabled("this fails because non primitives must be converted to bytes before serializing")
  internal fun `can convert non primitive type to bytes `() {
//    * Convert the given {@code value} into a {@link MetaDataValue}, attempting to maintain the source type as much as
//    * possible in the returned {@link MetaDataValue}.
//    ....
//    * <li>Any other object is serialized and stored as bytes</li>
//    * </ul>
    val now = LocalDate.now()
    val axon = MetaData.from(mapOf(
      "date" to now
    ))

    var avro: AvroMetaData = AvroMetaDataExt.convertToAvro(axon)
    // serialize/deserialize
    val buffer = avro.toByteBuffer()
    avro = AvroMetaData.fromByteBuffer(buffer)

    // check values
    assertThat(avro.values["date"]).isEqualTo(now)
  }
}
