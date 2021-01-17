package io.holixon.axon.avro.common

import io.holixon.axon.avro.common.ext.SchemaExt.extractSchemaInfo
import io.holixon.axon.avro.common.type.AvroPayloadAndSchema
import io.holixon.axon.avro.common.type.AvroSchemaInfo
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import org.apache.avro.Schema
import java.util.*
import java.util.concurrent.ConcurrentHashMap

typealias AvroSingleObjectEncoded = ByteArray
typealias SchemaId = Long
typealias SchemaRevision = String

object AvroCommon {

  /**
   * Determines the revision of a given schema by reading the String value of the given object-property.
   */
  fun propertyBasedSchemaRevisionResolver(propertyKey: String): SchemaRevisionResolver = object : SchemaRevisionResolver {
    override fun apply(schema: Schema): Optional<SchemaRevision> = Optional.ofNullable(schema.getObjectProp(propertyKey) as String?)
  }

  /**
   * Data class implementing [AvroSchemaInfo].
   */
  data class AvroSchemaInfoData(
    override val context: String,
    override val name: String,
    override val revision: SchemaRevision?
  ) : AvroSchemaInfo

  /**
   * Data class implementing [AvroSchemaWithId].
   */
  data class AvroSchemaWithIdData(
    override val id: SchemaId,
    override val schema: Schema,
    override val revision: SchemaRevision? = null
  ) : AvroSchemaWithId {
    override val context: String = schema.namespace
    override val name: String = schema.name
  }

  data class AvroPayloadAndSchemaData(
    override val schema: AvroSchemaWithId,
    override val payload: AvroSingleObjectEncoded
  ) : AvroPayloadAndSchema {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as AvroPayloadAndSchemaData

      if (schema != other.schema) return false
      if (!payload.contentEquals(other.payload)) return false

      return true
    }

    override fun hashCode(): Int {
      var result = schema.hashCode()
      result = 31 * result + payload.contentHashCode()
      return result
    }
  }

  class InMemoryAvroSchemaRegistry(
    private val store: ConcurrentHashMap<Long, Pair<AvroSchemaInfo, Schema>> = ConcurrentHashMap(),
    val schemaIdSupplier: SchemaIdSupplier,
    val schemaRevisionResolver: SchemaRevisionResolver
  ) : AvroSchemaRegistry, AutoCloseable {

    override fun register(schema: Schema): AvroSchemaWithId {
      val info = schema.extractSchemaInfo(schemaRevisionResolver)

      return findByInfo(info)
        .orElseGet {
          val id = schemaIdSupplier.apply(schema)
          store[id] = info to schema
          findById(id).get()
        }
    }

    override fun findById(globalId: Long): Optional<AvroSchemaWithId> = Optional.ofNullable(
      store[globalId]).map { AvroSchemaWithIdData(globalId, it.second, it.first.revision) }

    override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> = Optional.ofNullable(store.entries
      .filter { it.value.first == info }
      .map { it.toSchemaData() }
      .firstOrNull())

    override fun findByContextAndName(context: String, name: String): List<AvroSchemaWithId> = store
      .filter { it.value.first.name == name }
      .filter { it.value.first.context == context }
      .map { it.toSchemaData() }

    override fun findAll(): List<AvroSchemaWithId> = store.map { it.toSchemaData() }

    override fun close() {
      store.clear()
    }

    private fun Map.Entry<Long, Pair<AvroSchemaInfo, Schema>>.toSchemaData() = AvroSchemaWithIdData(key, value.second)

  }
}
