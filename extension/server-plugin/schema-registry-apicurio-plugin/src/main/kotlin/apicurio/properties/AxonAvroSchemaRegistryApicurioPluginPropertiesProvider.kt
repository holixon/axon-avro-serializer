package io.holixon.axon.avro.registry.plugin.apicurio.properties

import io.holixon.axon.avro.registry.plugin.ContextName

/**
 * Provides properties for different contexts.
 */
fun interface AxonAvroSchemaRegistryApicurioPluginPropertiesProvider {

  companion object {
    const val DEFAULT_CONEXT = "default"
  }

  fun get(context: ContextName): AxonAvroSchemaRegistryApicurioPluginProperties
}
