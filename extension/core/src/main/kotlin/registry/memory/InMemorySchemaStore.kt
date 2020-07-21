package io.toolisticon.axon.avro.registry.memory

import io.toolisticon.axon.avro.registry.SchemaSerializedType
import io.toolisticon.axon.avro.registry.SchemaStore
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.axonframework.serialization.SimpleSerializedType
import java.util.concurrent.ConcurrentHashMap


class InMemorySchemaStore : SchemaStore, AutoCloseable {

  val schemasById: MutableMap<Int, SchemaSerializedType> = ConcurrentHashMap()
  val schemasByNameAndVersion: MutableMap<SimpleSerializedType, SchemaSerializedType> = ConcurrentHashMap()
  val schemasByParsingForm: MutableMap<String, SchemaSerializedType> = ConcurrentHashMap()

  override fun add(schemaSerializedType: SchemaSerializedType) {
    schemasById[schemaSerializedType.id] = schemaSerializedType
    schemasByNameAndVersion[schemaSerializedType.serializedType] = schemaSerializedType
    schemasByParsingForm[SchemaNormalization.toParsingForm(schemaSerializedType.schema)] = schemaSerializedType
  }

  override fun getAll(): List<SchemaSerializedType> = schemasById.values.toList()

  override operator fun get(id: Int): SchemaSerializedType = requireNotNull(schemasById[id]) { "Could not find version with id=$id" }

  override fun get(simpleSerializedType: SimpleSerializedType): SchemaSerializedType = requireNotNull(schemasByNameAndVersion[simpleSerializedType]) { "Could not find version with name=${simpleSerializedType.name} and revision=${simpleSerializedType.revision}" }

  override fun getMetadata(schema: Schema): SchemaSerializedType {
    val parsingForm: String = SchemaNormalization.toParsingForm(schema)

    return requireNotNull(schemasByParsingForm[parsingForm]) { "Could not find metadata for schema.\nParsing form: $parsingForm" }
  }

  fun getAllSchemas() = schemasById.values.toSet()

  override fun close() {
    schemasById.clear()
    schemasByNameAndVersion.clear()
    schemasByParsingForm.clear()
  }

}
