package io.holixon.axon.avro.serializer.type

import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.axon.avro.serializer.ext.TypeExt.isAvroSingleObjectEncoded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSchemaSingleObjectSerializedObjectTest {

  @Test
  internal fun `create from sample event revision 4711`() {
    val bytes = SampleEvent("foo").toByteArray()
    val serializedObject = AvroSchemaSingleObjectSerializedObject(bytes, SampleEvent.getClassSchema())

    assertThat(serializedObject.data).isEqualTo(bytes)
    assertThat(serializedObject.type.revision).isEqualTo("4711")
    assertThat(serializedObject.type.name).isEqualTo("test.fixture.SampleEvent")
    assertThat(serializedObject.contentType.isAvroSingleObjectEncoded()).isTrue
  }
}
