package io.holixon.axon.avro.serializer.plugin.properties

import io.axoniq.axonserver.plugin.AttributeType
import io.axoniq.axonserver.plugin.PluginPropertyDefinition
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.converter.DefaultSingleObjectToJsonConverter
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.axon.avro.serializer.plugin.ContextName

fun interface AxonAvroSerializerPluginPropertiesProvider {
  fun get(context: ContextName): AxonAvroSerializerPluginProperties
}

data class AxonAvroSerializerPluginProperties(
  val host: String,
  val port: Int,
  val https: Boolean = false
) {
  companion object {
    val EMPTY = AxonAvroSerializerPluginProperties("", 0, false)
    const val KEY_HOST = "host"
    const val KEY_PORT = "port"

    const val DEFAULT_HOST = "registry"
    const val DEFAULT_PORT = 8080

    val PROPERTY_HOST: PluginPropertyDefinition = PluginPropertyDefinition.newBuilder(KEY_HOST, "Apicurio Host")
      .defaultValue(DEFAULT_HOST)
      .build()
    val PROPERTY_PORT: PluginPropertyDefinition = PluginPropertyDefinition.newBuilder(KEY_PORT, "Apicurio Port")
      .type(AttributeType.INTEGER)
      .defaultValue(DEFAULT_PORT)
      .build()


    operator fun invoke(map: Map<String, Any?>? = null): AxonAvroSerializerPluginProperties = if (map != null)
      AxonAvroSerializerPluginProperties(
        host = map.getOrDefault(KEY_HOST, DEFAULT_HOST) as String,
        port = map.getOrDefault(KEY_PORT, DEFAULT_PORT) as Int
      )
    else
      EMPTY

  }

  val jsonConverter by lazy {
    DefaultSingleObjectToJsonConverter(apicurioSchemaRegistry.schemaResolver())
  }

  val apicurioSchemaRegistry by lazy {
    ApicurioAvroSchemaRegistry(
      client = AvroAdapterApicurioRest.registryRestClient(host, port, https),
      schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
      schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
    )
  }
}
