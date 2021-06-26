package io.holixon.axon.avro.serializer.type

import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.AvroAdapterDefault.toAvroSchemaWithId
import org.apache.avro.Schema
import org.axonframework.serialization.SerializedObject
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType
import java.util.*

/**
 * Data class representing an entry in a schema registry.
 */
class AvroSchemaSerializedType(
  val schemaWithId: AvroSchemaWithId
) : SimpleSerializedType(
  schemaWithId.canonicalName,
  schemaWithId.revision
) {

  constructor(schema: Schema) : this(schema.toAvroSchemaWithId())

  val simpleSerializedType by lazy { SimpleSerializedType(name, revision) }

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (this === other) return true
    if (other is SimpleSerializedType) {
      return other.equals(simpleSerializedType)
    }

    return false
  }



  override fun hashCode(): Int = super.hashCode()

}
