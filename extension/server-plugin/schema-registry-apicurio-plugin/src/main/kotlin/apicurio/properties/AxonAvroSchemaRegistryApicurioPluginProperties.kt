package io.holixon.axon.avro.registry.plugin.apicurio.properties

import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest

data class AxonAvroSchemaRegistryApicurioPluginProperties(
  val host: String,
  val port: Int,
  val https: Boolean = false
) {
  companion object {

    val EMPTY = AxonAvroSchemaRegistryApicurioPluginProperties("", 0, false)
    const val KEY_HOST = "host"
    const val KEY_PORT = "port"
    const val KEY_USE_SSL = "use-ssl"
    const val DEFAULT_HOST = "registry"
    const val DEFAULT_PORT = 8080
    const val DEFAULT_USE_SSL = false

    operator fun invoke(map: Map<String, Any?>? = null): AxonAvroSchemaRegistryApicurioPluginProperties = if (map != null)
      AxonAvroSchemaRegistryApicurioPluginProperties(
        host = map.getOrDefault(KEY_HOST, DEFAULT_HOST) as String,
        port = map.getOrDefault(KEY_PORT, DEFAULT_PORT) as Int,
        https = map.getOrDefault(KEY_USE_SSL, DEFAULT_USE_SSL) as Boolean
      )
    else
      EMPTY
  }

  val schemaRegistry by lazy {
    ApicurioAvroSchemaRegistry(
      client = AvroAdapterApicurioRest.registryRestClient(host, port, https),
      schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
      schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
    )
  }
}
