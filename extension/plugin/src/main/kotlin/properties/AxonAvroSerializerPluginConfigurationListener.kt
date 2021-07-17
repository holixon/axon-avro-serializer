package io.holixon.axon.avro.serializer.plugin.properties

import io.axoniq.axonserver.plugin.Configuration
import io.axoniq.axonserver.plugin.ConfigurationListener
import io.holixon.axon.avro.serializer.plugin.AxonAvroSerializerPlugin
import io.holixon.axon.avro.serializer.plugin.ContextName
import io.holixon.axon.avro.serializer.plugin.properties.AxonAvroSerializerPluginProperties.Companion.PROPERTY_HOST
import io.holixon.axon.avro.serializer.plugin.properties.AxonAvroSerializerPluginProperties.Companion.PROPERTY_PORT
import mu.KLogging
import java.lang.IllegalArgumentException

/**
 * Allows configuration of plugin via [ConfigurationListener], see [configuring a plugin](https://docs.axoniq.io/reference-guide/v/master/axon-server/administration/plugins#configuring-a-plugin).
 */
class AxonAvroSerializerPluginConfigurationListener : ConfigurationListener, AxonAvroSerializerPluginPropertiesProvider {
  companion object : KLogging()

  private val configuration: Configuration by lazy {
    Configuration(
      listOf(PROPERTY_HOST, PROPERTY_PORT),
      AxonAvroSerializerPlugin.PLUGIN_NAME
    )
  }

  private val propertiesPerContext: MutableMap<ContextName, AxonAvroSerializerPluginProperties> = mutableMapOf()
  override fun configuration(): Configuration = configuration

  override fun updated(context: String, configuration: Map<String, *>?) {
    val updated = propertiesPerContext.put(context, AxonAvroSerializerPluginProperties(configuration))
    logger.info { "updated: context=$context, configuration=$updated" }
  }

  override fun removed(context: String) {
    val removed = propertiesPerContext.remove(context)
    logger.info { "removed: context=$context, configuration=$removed" }
  }

  override fun get(context: ContextName): AxonAvroSerializerPluginProperties =
    propertiesPerContext[context] ?: throw IllegalArgumentException("context[$context] not configured")

  override fun toString(): String = "AxonAvroSerializerPluginConfigurationListener(propertiesPerContext=$propertiesPerContext)"
}
