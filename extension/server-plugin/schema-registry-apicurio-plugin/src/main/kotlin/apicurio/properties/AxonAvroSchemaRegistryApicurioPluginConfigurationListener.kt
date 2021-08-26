package io.holixon.axon.avro.registry.plugin.apicurio.properties

import io.axoniq.axonserver.plugin.AttributeType
import io.axoniq.axonserver.plugin.Configuration
import io.axoniq.axonserver.plugin.ConfigurationListener
import io.axoniq.axonserver.plugin.PluginPropertyDefinition
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginPropertiesProvider.Companion.DEFAULT_CONTEXT
import io.holixon.axon.avro.serializer.plugin.api.ContextName
import mu.KLogging

/**
 * Allows configuration of plugin via [ConfigurationListener], see [configuring a plugin](https://docs.axoniq.io/reference-guide/v/master/axon-server/administration/plugins#configuring-a-plugin).
 */
class AxonAvroSchemaRegistryApicurioPluginConfigurationListener
  : ConfigurationListener, AxonAvroSchemaRegistryApicurioPluginPropertiesProvider {

  companion object : KLogging()

  private val propertiesPerContext: MutableMap<ContextName, AxonAvroSchemaRegistryApicurioPluginProperties> = mutableMapOf()

  override fun configuration(): Configuration = Configuration(
    listOf(
      PluginPropertyDefinition.newBuilder(AxonAvroSchemaRegistryApicurioPluginProperties.KEY_HOST, "Apicurio Host")
        .description("Apicurio Host")
        .defaultValue(AxonAvroSchemaRegistryApicurioPluginProperties.DEFAULT_HOST)
        .build(),
      PluginPropertyDefinition.newBuilder(AxonAvroSchemaRegistryApicurioPluginProperties.KEY_PORT, "Apicurio Port")
        .description("Apicurio Port")
        .type(AttributeType.INTEGER)
        .defaultValue(AxonAvroSchemaRegistryApicurioPluginProperties.DEFAULT_PORT)
        .build(),
      PluginPropertyDefinition.newBuilder(AxonAvroSchemaRegistryApicurioPluginProperties.KEY_USE_SSL, "Use SSL")
        .description("Use SSL")
        .type(AttributeType.BOOLEAN)
        .defaultValue(AxonAvroSchemaRegistryApicurioPluginProperties.DEFAULT_USE_SSL)
        .build()
    ),
    "Apicurio Configuration"
  )

  override fun get(context: ContextName): AxonAvroSchemaRegistryApicurioPluginProperties =
    propertiesPerContext[context] ?: if (context == DEFAULT_CONTEXT) {
      AxonAvroSchemaRegistryApicurioPluginProperties(mapOf())
    } else throw IllegalArgumentException("context[$context] not configured")

  override fun updated(context: String, configuration: Map<String, *>?) {
    val new = AxonAvroSchemaRegistryApicurioPluginProperties(configuration)
    val updated = propertiesPerContext.put(context, new)
    logger.debug { "Updated properties for context=$context, old configuration=$updated, new configuration=$new" }
    if (logger.isDebugEnabled) {
      dumpSchemas(new)
    }
  }

  override fun removed(context: String) {
    val removed = propertiesPerContext.remove(context)
    logger.debug { "Removed configuration for context=$context, configuration=$removed" }
  }

  override fun toString(): String = "Context Configurations: $propertiesPerContext"

  private fun dumpSchemas(properties: AxonAvroSchemaRegistryApicurioPluginProperties) {
    logger.debug { "All schema: ${properties.schemaRegistry.findAll()}" }
  }

}

