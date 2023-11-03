package io.holixon.axon.avro.registry.plugin.apicurio

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.converter.SingleObjectToJsonConverter
import io.holixon.avro.adapter.common.converter.DefaultSingleObjectToJsonConverter
import io.holixon.axon.avro.registry.plugin.apicurio.cache.CachingSchemaResolver
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginPropertiesProvider
import io.holixon.axon.avro.serializer.plugin.api.ContextName
import io.holixon.axon.avro.serializer.plugin.api.SingleObjectToJsonConverterProvider
import mu.KLogging

/**
 * Provider for the JSON converter using Apicurio registry.
 */
class ApicurioSingleObjectToJsonConverterProvider(
  private val propertiesProvider: AxonAvroSchemaRegistryApicurioPluginPropertiesProvider
) : SingleObjectToJsonConverterProvider {

  companion object: KLogging()

  override fun get(contextName: ContextName): SingleObjectToJsonConverter {
    logger.info { "Retrieving a converter for context $contextName" }
    val registry = propertiesProvider.get(contextName).schemaRegistry
    logger.info { "Registry: $registry" }
    val cachingSchemaResolver = CachingSchemaResolver(registry.schemaResolver())
    logger.info { "Resolver: $cachingSchemaResolver" }
    return DefaultSingleObjectToJsonConverter(cachingSchemaResolver)
  }

  override fun toString(): String = "ApicurioSingleObjectToJsonConverterProvider[$propertiesProvider]"

}
