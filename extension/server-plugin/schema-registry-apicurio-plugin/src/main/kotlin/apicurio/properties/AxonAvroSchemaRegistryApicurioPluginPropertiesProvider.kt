package io.holixon.axon.avro.registry.plugin.apicurio.properties

import io.holixon.axon.avro.serializer.plugin.api.ContextName

/**
 * Provides properties for different contexts.
 */
fun interface AxonAvroSchemaRegistryApicurioPluginPropertiesProvider {

  companion object {
    /**
     * Default context.
     */
    const val DEFAULT_CONEXT = "default"
  }

  fun get(context: ContextName): AxonAvroSchemaRegistryApicurioPluginProperties
}
