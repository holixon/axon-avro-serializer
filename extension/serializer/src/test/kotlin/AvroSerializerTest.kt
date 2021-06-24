package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import mu.KLogging
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.SerializedObject
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
  internal fun `can serialize genericRecord to single object`() {
    val record = GenericData.Record(SampleEvent.getClassSchema()).apply {
      put("value", "foo")
    }
    val serialized: SerializedObject<AvroSingleObjectEncoded /* = kotlin.ByteArray */> = serializer.serialize(record, AvroSingleObjectEncoded::class.java)

    assertThat(serialized.type.name).isEqualTo(SampleEvent.getClassSchema().fullName)
    assertThat(serialized.type.revision).isEqualTo(SampleEvent.getClassSchema().revision)
    assertThat(serialized.contentType).isEqualTo(AvroSingleObjectEncoded::class.java)
    assertThat(serialized.data.toHexString()).isEqualTo("[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]")
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

