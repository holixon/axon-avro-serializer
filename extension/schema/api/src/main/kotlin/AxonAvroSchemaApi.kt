package io.holixon.axon.avro.schema.api

import io.holixon.axon.avro.schema.api.ext.SchemaExt.info
import io.holixon.axon.avro.schema.api.ext.SchemaExt.revision
import org.apache.avro.Schema
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Provides constants and default implementations for the interfaces defined in this module.
 */
object AxonAvroSchemaApi {
  const val PROP_REVISION = "revision"

  fun avroSchemaInfo(context:String, name:String, revision: String?) = AxonAvroSchemaInfoData(context = context, name = name, revision = revision)
  fun avroSchema(globalId: Long, schema: Schema) = AxonAvroSchemaData(globalId = globalId, schema = schema)

  data class AxonAvroSchemaInfoData(
    override val context: String,
    override val name: String,
    override val revision: String?
  ) : AxonAvroSchemaInfo

  data class AxonAvroSchemaData(
    override val globalId: Long,
    override val schema: Schema
  ) : AxonAvroSchema {
    override val context: String = schema.namespace
    override val name: String = schema.name
    override val revision: String? = schema.revision
  }

  class InMemoryAxonAvroSchemaRegistry(
    val idGenerator: () -> Long,
    val store : MutableMap<Long, Pair<AxonAvroSchemaInfo, Schema>>
  ) : AxonAvroSchemaRegistry, AutoCloseable {
    companion object {
      fun createDefault(): InMemoryAxonAvroSchemaRegistry {
        val ids = AtomicLong(0)
        return InMemoryAxonAvroSchemaRegistry(
          idGenerator = ids::incrementAndGet,
          store = ConcurrentHashMap()
        )
      }
    }

    override fun register(schema: Schema): AxonAvroSchema {
      val info = schema.info
      return findByInfo(info)
        .orElseGet {
          val id = idGenerator()
          store[id] = info to schema
          findById(id).get()
        }
    }

    override fun findById(globalId: Long): Optional<AxonAvroSchema> = Optional.ofNullable(
      store[globalId]?.let { AxonAvroSchemaData(globalId, it.second) }
    )

    override fun findByInfo(info: AxonAvroSchemaInfo): Optional<AxonAvroSchema> = Optional.ofNullable(store.entries
      .filter { it.value.first == info }
      .map { it.toSchemaData() }
      .firstOrNull())

    override fun findByContextAndName(context: String, name: String): List<AxonAvroSchema> = store
      .filter { it.value.first.name == name }
      .filter { it.value.first.context == context }
      .map { it.toSchemaData() }

    private fun Map.Entry<Long, Pair<AxonAvroSchemaInfo, Schema>>.toSchemaData() = AxonAvroSchemaData(key, value.second)

    override fun close() {
      store.clear()
    }
  }
}
