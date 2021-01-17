package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.common.type.AvroSchemaWithId
import io.holixon.axon.avro.serializer.ext.SchemaExt.info
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import org.apache.avro.Schema
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType

/**
 * Data class representing an entry in a schema registry.
 */
data class SchemaSerializedType(
  val globalId: Long,
  val serializedType: SimpleSerializedType,
  val schema: Schema
) : SerializedType by serializedType {

  constructor(globalId: Long, name: String, revision: String?, schema: Schema) : this(
    globalId = globalId,
    serializedType = SimpleSerializedType(name, revision),
    schema = schema
  )

  constructor(globalId: Long, schema: Schema) : this(
    globalId = globalId,
    name = schema.info.canonicalName,
    revision = schema.revision,
    schema = schema
  )

  constructor(arvoSchema: AvroSchemaWithId) : this(
    globalId = arvoSchema.id,
    schema = arvoSchema.schema
  )

}
