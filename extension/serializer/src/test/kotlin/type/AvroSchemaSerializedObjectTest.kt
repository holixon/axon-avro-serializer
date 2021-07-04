package io.holixon.axon.avro.serializer.type

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.axon.avro.serializer.ext.TypeExt.isAvroSingleObjectEncoded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent


internal class AvroSchemaSerializedObjectTest {

  @Test
  internal fun `create for schema sample event`() {
    val bytes = SampleEvent("foo").toByteArray()
    val serializedObject = AvroSchemaSerializedObject<AvroSingleObjectEncoded>(bytes, AvroSingleObjectEncoded::class.java, SampleEvent.getClassSchema())

    assertThat(serializedObject.data).isEqualTo(bytes)
    assertThat(serializedObject.type.revision).isEqualTo("4711")
    assertThat(serializedObject.type.name).isEqualTo("test.fixture.SampleEvent")
    assertThat(serializedObject.contentType.isAvroSingleObjectEncoded()).isTrue
  }
}
