package io.holixon.axon.avro.registry.plugin

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.converter.SingleObjectToJsonConverter
import io.holixon.avro.adapter.common.converter.DefaultSingleObjectToJsonConverter
import io.holixon.axon.avro.registry.plugin.apicurio.cache.CachingSchemaResolver
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginPropertiesProvider

/**
 * Provider for the JSON converter using Apicurio registry.
 */
class ApicurioSingleObjectToJsonConverterProvider(
  private val propertiesProvider: AxonAvroSchemaRegistryApicurioPluginPropertiesProvider
) : SingleObjectToJsonConverterProvider {

  override fun get(contextName: ContextName): SingleObjectToJsonConverter {
    val registry = propertiesProvider.get(contextName).schemaRegistry
    val cachingSchemaResolver = CachingSchemaResolver(registry.schemaResolver())
    return DefaultSingleObjectToJsonConverter(cachingSchemaResolver)
  }

  override fun toString(): String = "ApicurioSingleObjectToJsonConverterProvider[$propertiesProvider]"

}
