package io.toolisticon.axon.avro.registry

import org.apache.avro.Schema
import org.axonframework.serialization.SimpleSerializedType

/**
 * Read access to schema repository.
 */
interface SchemaProvider {
  fun get(id: Int): SchemaSerializedType

  fun get(schemaName: String, schemaVersion: String?): SchemaSerializedType = get(SimpleSerializedType(schemaName, schemaVersion))

  fun get(simpleSerializedType: SimpleSerializedType) : SchemaSerializedType

  fun getMetadata(schema: Schema): SchemaSerializedType
}
