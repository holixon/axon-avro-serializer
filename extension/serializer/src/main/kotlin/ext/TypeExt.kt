package io.holixon.axon.avro.serializer.ext

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.UnknownSerializedType

object TypeExt {

  fun Class<*>.isAvroSingleObjectEncoded() = this == AvroSingleObjectEncoded::class.java
  fun Class<*>.isUnknown() = UnknownSerializedType::class.java.isAssignableFrom(this)

  fun <T:Any> SerializedObject<T>.isEmpty() = this.type == SerializedType.emptyType()
  fun <T:Any> SerializedObject<T>.isUnkown() = this.type == UnknownSerializedType::class.java
}
