package io.holixon.axon.avro.serializer

import io.toolisticon.avro.adapter.common.AvroAdapterDefault
import mu.KLogging
import org.apache.avro.specific.SpecificRecordBase
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.serialization.SerializedObject
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSerializerTest {

  companion object : KLogging()

  private val registry = AvroAdapterDefault.inMemorySchemaRepository()

  private val serializer = AvroSerializer.builder()
    .schemaRegistry(registry)
    .build()

  private val event = SampleEvent("foo")

  private val writerSchema = registry.register(SampleEvent.getClassSchema())


  @Test
  internal fun `canSerialize to ByteArray and SpecificRecordBase`() {
    assertThat(serializer.canSerializeTo(ByteArray::class.java)).isTrue
    assertThat(serializer.canSerializeTo(SpecificRecordBase::class.java)).isTrue
  }

  @Test
  internal fun `can serialize to byteArray`() {
    val serialized: SerializedObject<ByteArray> = serializer.serialize(event, ByteArray::class.java)

    val deserialized: SampleEvent? = serializer.deserialize(serialized)

    assertThat(deserialized).isEqualTo(event)
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

