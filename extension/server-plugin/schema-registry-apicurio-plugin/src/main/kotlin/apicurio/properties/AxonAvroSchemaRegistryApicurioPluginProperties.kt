package io.holixon.axon.avro.registry.plugin.apicurio.properties

import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest

/**
 * Configuration properties for the Apicurio Axon Server plugin.
 */
data class AxonAvroSchemaRegistryApicurioPluginProperties(
  val host: String,
  val port: Int,
  val https: Boolean = false
) {
  companion object {

    /**
     * Empty properties.
     */
    val EMPTY = AxonAvroSchemaRegistryApicurioPluginProperties("", 0, false)

    /**
     * Property key for the hostname.
     */
    const val KEY_HOST = "host"

    /**
     * Property key for the port.
     */
    const val KEY_PORT = "port"

    /**
     * Property key for usage of HTTPS.
     */
    const val KEY_USE_SSL = "use-ssl"

    /**
     * Default hostname.
     */
    const val DEFAULT_HOST = "registry"

    /**
     * Default port.
     */
    const val DEFAULT_PORT = 8080

    /**
     * Default for HTTPS usage.
     */
    const val DEFAULT_USE_SSL = false

    /**
     * Factory methods to create properties out of map.
     */
    operator fun invoke(map: Map<String, Any?>? = null): AxonAvroSchemaRegistryApicurioPluginProperties = if (map != null)
      AxonAvroSchemaRegistryApicurioPluginProperties(
        host = map.getOrDefault(KEY_HOST, DEFAULT_HOST) as String,
        port = map.getOrDefault(KEY_PORT, DEFAULT_PORT) as Int,
        https = map.getOrDefault(KEY_USE_SSL, DEFAULT_USE_SSL) as Boolean
      )
    else
      EMPTY
  }

  /**
   * Schema registry configured with given properties.
   */
  val schemaRegistry by lazy {
    ApicurioAvroSchemaRegistry(
      client = AvroAdapterApicurioRest.registryRestClient(host, port, https),
      schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
      schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
    )
  }
}
