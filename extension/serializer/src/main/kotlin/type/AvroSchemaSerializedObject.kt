package io.holixon.axon.avro.serializer.type

import org.apache.avro.Schema
import org.axonframework.serialization.SimpleSerializedObject

open class AvroSchemaSerializedObject<T: Any>(data : T, dataType : Class<T>, serializedType: AvroSchemaSerializedType) : SimpleSerializedObject<T>(data, dataType,serializedType) {

  constructor(data:T, dataType: Class<T>, schema:Schema) : this(data,dataType, AvroSchemaSerializedType(schema))

  val simpleSerializedObject by lazy { SimpleSerializedObject<T>(data, dataType, serializedType) }

  override fun toString(): String = "AvroSchemaSerializedObject[${simpleSerializedObject.type}]"

}
