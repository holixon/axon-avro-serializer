package io.toolisticon.axon.avro.registry

/**
 * Write access to schema repository.
 */
interface SchemaStore : SchemaProvider {

  @Throws(Exception::class)
  fun add(schemaSerializedType: SchemaSerializedType)

  fun getAll() : List<SchemaSerializedType>
}
