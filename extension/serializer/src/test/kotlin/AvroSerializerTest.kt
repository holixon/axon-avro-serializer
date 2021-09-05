package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import io.holixon.axon.avro.serializer.metadata.AvroMetaDataExt
import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
import mu.KLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.*
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSerializerTest {

  companion object : KLogging()

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()
    .apply {
      register(SampleEvent.getClassSchema())
    }

  private val serializer = AvroSerializer.builder()
    .schemaRegistry(registry)
    .build()

  private val event = SampleEvent("foo")

  @Test
  internal fun `canSerialize to ByteArray GenericDataRecord and SpecificRecordBase`() {
    assertThat(serializer.canSerializeTo(ByteArray::class.java)).isTrue
    assertThat(serializer.canSerializeTo(GenericData.Record::class.java)).isTrue
  }

  @Test
  internal fun `can serialize to GenericDataRecord`() {
    assertThat(serializer.canSerializeTo(GenericData.Record::class.java)).isTrue
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

  @Test
  internal fun `serializes metaData to singleObjectEncoded`() {
    val axonMetaData = MetaData.with("foo", "bar")
    val expectedBytes: AvroSingleObjectEncoded = AvroMetaDataExt.convertToAvro(axonMetaData).toByteArray()

    val serialized = serializer.serialize(axonMetaData, AvroSingleObjectEncoded::class.java)
    assertThat(SerializedMetaData.isSerializedMetaData(serialized)).isTrue

    assertThat(serialized.data.toHexString()).isEqualTo(expectedBytes.toHexString())
  }

  @Test
  internal fun `deserializes singleObjectEncoded to metaData`() {
    val origMetaData = MetaData.with("foo", "bar")
    val serialized: SerializedObject<AvroSingleObjectEncoded /* = kotlin.ByteArray */> =
      serializer.serialize(origMetaData, AvroSingleObjectEncoded::class.java)

    assertThat(serialized.type.name).isEqualTo(MetaData::class.java.canonicalName)

    val deserialized: MetaData = serializer.deserialize(serialized) ?: throw IllegalStateException("can not deserialize")

    assertThat(deserialized).isEqualTo(origMetaData)
  }

  @Test
  internal fun `builder functions`() {
    val allDefaults = AvroSerializer.builder()
      .converter(ChainingConverter())
      .revisionResolver(SchemaBasedRevisionResolver())
      .schemaRegistry(AvroAdapterDefault.inMemorySchemaRegistry())
      .decoderSpecificRecordClassResolver(AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver)
      .schemaIncompatibilityResolver(AvroAdapterDefault.defaultSchemaCompatibilityResolver)
      .build()

    assertThat(allDefaults.canSerializeTo(AvroSingleObjectEncoded::class.java)).isTrue
  }
}

