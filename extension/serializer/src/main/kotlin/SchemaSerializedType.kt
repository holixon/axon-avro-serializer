package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSchemaId
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType

/**
 * Data class representing an entry in a schema registry.
 */
data class SchemaSerializedType(
  val serializedType: SimpleSerializedType,
  private val schemaWithId: AvroSchemaWithId
) : SerializedType by serializedType {

  val schemaId: AvroSchemaId = schemaWithId.schemaId
  val schema = schemaWithId.schema

  constructor(arvoSchema: AvroSchemaWithId) : this(
    serializedType = SimpleSerializedType(arvoSchema.canonicalName, arvoSchema.revision),
    schemaWithId = arvoSchema
  )

}
