package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.common.SchemaId
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType

/**
 * Data class representing an entry in a schema registry.
 */
data class SchemaSerializedType(
  val serializedType: SimpleSerializedType,
  private val schemaWithId: AvroSchemaWithId
) : SerializedType by serializedType {

  val schemaId: SchemaId = schemaWithId.id
  val schema = schemaWithId.schema

  constructor(arvoSchema: AvroSchemaWithId) : this(
    serializedType = SimpleSerializedType(arvoSchema.canonicalName, arvoSchema.revision),
    schemaWithId = arvoSchema
  )

}