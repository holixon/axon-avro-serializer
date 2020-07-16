package io.toolisticon.axon.avro.serializer

import org.axonframework.serialization.Converter
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.Serializer

class AvroSerializer() : Serializer {

  companion object {

    @JvmStatic
    fun builder() = Builder()

    // secondary constructor
    operator fun invoke(builder: Builder) = AvroSerializer()
  }

  override fun classForType(type: SerializedType): Class<*> = TODO("Not yet implemented")

  override fun typeForClass(type: Class<*>): SerializedType = TODO("Not yet implemented")

  override fun <T : Any> canSerializeTo(type: Class<T>): Boolean = TODO("Not yet implemented")

  override fun <T : Any> serialize(data: Any, expectedRepresentation: Class<T>): SerializedObject<T> = TODO("Not yet implemented")

  override fun <S : Any, T : Any> deserialize(serializedObject: SerializedObject<S>): T = TODO("Not yet implemented")

  override fun getConverter(): Converter = TODO("Not yet implemented")


  class Builder {

    fun build() = AvroSerializer(this)

  }
}
