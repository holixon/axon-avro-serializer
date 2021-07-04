package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import mu.KLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SimpleSerializedObject
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.UnknownSerializedType
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSerializerTest {

  companion object : KLogging()

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  private val serializer = AvroSerializer.builder()
    .schemaRegistry(registry)
    .build()

  private val event = SampleEvent("foo")

  private val writerSchema = registry.register(SampleEvent.getClassSchema())


  @Test
  internal fun `canSerialize to ByteArray GenericDataRecord and SpecificRecordBase`() {
    assertThat(serializer.canSerializeTo(ByteArray::class.java)).isTrue
    assertThat(serializer.canSerializeTo(GenericData.Record::class.java)).isTrue
  }

  @Test
  internal fun `can serialize to GenericDataRecord`() {
    val serialized: SerializedObject<GenericData.Record> = serializer.serialize(event, GenericData.Record::class.java)

    val deserialized: SampleEvent? = serializer.deserialize(serialized)

    assertThat(deserialized).isEqualTo(event)
  }

  @Test
  internal fun `class for type`() {
    assertThat(serializer.classForType(SimpleSerializedType.emptyType())).isEqualTo(Void::class.java)
    assertThat(serializer.classForType(SimpleSerializedType("test.fixture.SampleEvent", "4711"))).isEqualTo(SampleEvent::class.java)
    assertThat(serializer.classForType(SimpleSerializedType("i.do.not.Exist", "1"))).isEqualTo(UnknownSerializedType::class.java)
  }

  @Test
  internal fun `can serialize genericRecord to single object`() {
    val record = SampleEvent.getClassSchema().createGenericRecord {
      put("value", "foo")
    }
    val serialized: SerializedObject<AvroSingleObjectEncoded /* = kotlin.ByteArray */> =
      serializer.serialize(record, AvroSingleObjectEncoded::class.java)

    assertThat(serialized.type.name).isEqualTo(SampleEvent.getClassSchema().fullName)
    assertThat(serialized.type.revision).isEqualTo(SampleEvent.getClassSchema().revision)
    assertThat(serialized.contentType).isEqualTo(AvroSingleObjectEncoded::class.java)
    assertThat(serialized.data.toHexString()).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
  }

  @Test
  internal fun `deserialize empty to null`() {
    assertThat(
      serializer.deserialize<String, String>(
        SimpleSerializedObject(
          "foo",
          String::class.java,
          SimpleSerializedType.emptyType()
        )
      )
    ).isNull()
  }

  @Test
  internal fun `deserialize unknown type`() {
    // this does not exist as a class on the class path. But it can be registered ...
    val dummySchema: Schema = registry.register(
      Schema.Parser().parse(
        """{
        "type": "record",
        "namespace": "serializer.test",
        "name": "DummyEvent",
        "revision": "6",
        "fields": [
          {
            "name": "value",
            "type": {
              "type": "string",
              "avro.java.string": "String"
            }
          }
        ]
      }
    """.trimIndent()
      )
    ).schema
    // ... and gerneic data  can be serialized
    val serialized = serializer.serialize(dummySchema.createGenericRecord { put("value", "hello") }, AvroSingleObjectEncoded::class.java)

    assertThat(serialized.type.name).isEqualTo(dummySchema.fullName)

    val unknown: UnknownSerializedType = serializer.deserialize(serialized)!!
    val record = unknown.readData(GenericData.Record::class.java)

    assertThat(record.schema).isEqualTo(dummySchema)
    assertThat(record["value"]).isEqualTo("hello")
  }

  //
//  @Test
//  void testSerializeAndDeserializeObject_ByteArrayFormat() {
//    SimpleSerializableType toSerialize = new SimpleSerializableType("first", time,
//      new SimpleSerializableType("nested"));
//
//    SerializedObject<byte[]> serialized = testSubject.serialize(toSerialize, byte[].class);
//
//    SimpleSerializableType actual = testSubject.deserialize(serialized);
//
//    assertEquals(toSerialize.getValue(), actual.getValue());
//    assertEquals(toSerialize.getNested().getValue(), actual.getNested().getValue());
//  }
  @Test
  internal fun name() {
    logger.info { "event: $event" }
    logger.info { "schema: ${event.schema}" }
  }


}

