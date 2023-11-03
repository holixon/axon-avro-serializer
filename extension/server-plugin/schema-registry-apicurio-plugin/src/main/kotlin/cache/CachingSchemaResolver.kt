package io.holixon.axon.avro.registry.plugin.apicurio.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSchemaWithId
import mu.KLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Resolvers using a local in-memory cache for the resolution.
 */
class CachingSchemaResolver(private val schemaResolver: AvroSchemaResolver) : AvroSchemaResolver {
  companion object : KLogging()

  private val cache = ConcurrentHashMap<AvroSchemaId, AvroSchemaWithId?>()

  override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    logger.info { "Requested $schemaId from cache" }
    return Optional.ofNullable(cache.computeIfAbsent(schemaId) {
      logger.info { "Cache miss, trying to get it from the registry..." }
      schemaResolver.apply(schemaId).orElse(null).also {
        logger.info { "resolving - schemaId=$schemaId, schema=$it" }
      }
    })
  }
}
