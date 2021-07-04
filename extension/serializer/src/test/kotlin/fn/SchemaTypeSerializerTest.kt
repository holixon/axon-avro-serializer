package io.holixon.axon.avro.serializer.fn

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.axon.avro.serializer.type.AvroSchemaSingleObjectSerializedObject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.axonframework.serialization.ChainingConverter
import org.axonframework.serialization.ContentTypeConverter
import org.axonframework.serialization.SerializationException
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class SchemaTypeSerializerTest {
  private val converter: ChainingConverter = ChainingConverter(SchemaTypeSerializerTest::class.java.classLoader).apply {
    registerConverter(object : ContentTypeConverter<AvroSingleObjectEncoded, String> {
      override fun expectedSourceType(): Class<AvroSingleObjectEncoded> = AvroSingleObjectEncoded::class.java
      override fun targetType(): Class<String> = String::class.java

      override fun convert(original: AvroSingleObjectEncoded): String = original.toHexString()
    })
  }

  private val genericRecordDateSerializer = SchemaTypeSerializer.genericRecordDataSerializer(converter)
  private val specificRecordDateSerializer = SchemaTypeSerializer.specificRecordDataSerializer(converter)

  private val genericRecord = SampleEvent.getClassSchema().createGenericRecord {
    put("value", "foo")
  }

  private val specificRecord = SampleEvent("foo")


  @Test
  internal fun `serialize generic record data to String`() {
    val string = genericRecordDateSerializer.serialize(genericRecord, String::class)

    assertThat(string.data).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
  }

  @Test
  internal fun `generic fail if expected type can not be converted`() {

    assertThatThrownBy { genericRecordDateSerializer.serialize(genericRecord, Int::class.java) }
      .isInstanceOf(SerializationException::class.java)
      .hasMessageContaining("Unable to serialize object into int")
  }

  @Test
  internal fun `specific fail if expected type can not be converted to int`() {
    assertThatThrownBy { specificRecordDateSerializer.serialize(specificRecord, Int::class.java) }
      .isInstanceOf(SerializationException::class.java)
      .hasMessageContaining("Unable to serialize object into int")
  }

  @Test
  internal fun `specific fix by registering int converter`() {

    // this converter sums up the int values of the bytes ... its stupid, but it shows how registering a custom converter
    // can overcome the "Unable to serialize" exception.
    val count = object : ContentTypeConverter<AvroSingleObjectEncoded, Int> {
      override fun expectedSourceType(): Class<AvroSingleObjectEncoded> = AvroSingleObjectEncoded::class.java
      override fun targetType(): Class<Int> = Int::class.java
      override fun convert(original: AvroSingleObjectEncoded): Int = original.map { it.toInt() }.sum()
    }

    converter.registerConverter(count)

    assertThat(specificRecordDateSerializer.serialize(specificRecord, Int::class).data).isEqualTo(99)

  }

  @Test
  internal fun `explicit genericDataRecord to  singleObject`() {

    val o = genericRecordDateSerializer.serializeToSingleObject(genericRecord)

    assertThat(o).isInstanceOf(AvroSchemaSingleObjectSerializedObject::class.java)
    assertThat(o.data.toHexString()).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
  }

  @Test
  internal fun `serialize generic record to single object`() {

    val o = genericRecordDateSerializer.serialize(genericRecord, AvroSingleObjectEncoded::class.java)

    assertThat(o).isInstanceOf(AvroSchemaSingleObjectSerializedObject::class.java)
    assertThat(o.data.toHexString()).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
  }

  @Test
  internal fun `serialize specific record to single object`() {

    val o = specificRecordDateSerializer.serialize(specificRecord, AvroSingleObjectEncoded::class.java)

    assertThat(o).isInstanceOf(AvroSchemaSingleObjectSerializedObject::class.java)
    assertThat(o.data.toHexString()).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
  }

}
