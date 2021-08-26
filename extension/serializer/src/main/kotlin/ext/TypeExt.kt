package io.holixon.axon.avro.serializer.ext

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.UnknownSerializedType

/**
 * Type helper functions.
 */
object TypeExt {

  /**
   * Checks if the type is an Avro Single Object Encoded.
   */
  fun Class<*>.isAvroSingleObjectEncoded() = this == AvroSingleObjectEncoded::class.java

  /**
   * Checks if the class is of unknown serialized type.
   */
  fun Class<*>.isUnknown() = UnknownSerializedType::class.java.isAssignableFrom(this)

  /**
   * Checks if the serialized object is an empty type.
   */
  fun <T:Any> SerializedObject<T>.isEmpty() = this.type == SerializedType.emptyType()

  /**
   * Checks if the serialized object is an unknown serialized type.
   */
  fun <T:Any> SerializedObject<T>.isUnknown() = this.type == UnknownSerializedType::class.java
}
