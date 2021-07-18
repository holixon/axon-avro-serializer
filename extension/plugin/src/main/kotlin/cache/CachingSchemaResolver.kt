package io.holixon.axon.avro.serializer.plugin.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaResolver
import mu.KLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CachingSchemaResolver(private val schemaResolver: SchemaResolver) : SchemaResolver {
  companion object : KLogging()

  val cache = ConcurrentHashMap<AvroSchemaId, AvroSchemaWithId?>()

  override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    return Optional.ofNullable(cache.computeIfAbsent(schemaId) {
      schemaResolver.apply(schemaId).orElse(null).also {
        logger.info { "resolving - schemaId=$schemaId, schema=$it" }
      }
    })
  }
}
