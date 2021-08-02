package io.holixon.axon.avro.registry.plugin.apicurio

import io.axoniq.axonserver.plugin.ConfigurationListener
import io.holixon.axon.avro.registry.plugin.ApicurioSingleObjectToJsonConverterProvider
import io.holixon.axon.avro.registry.plugin.SingleObjectToJsonConverterProvider
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginConfigurationListener
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginPropertiesProvider
import mu.KLogging
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration


/**
 * Plugin providing Apicurio schema registry
 */
class AxonSchemaRegistryApicurioPlugin : BundleActivator {

  companion object : KLogging()

  private val registrations = mutableSetOf<ServiceRegistration<*>>()

  override fun start(context: BundleContext) {

    // make the properties available to Axon Server by registering it as a ConfigurationListener
    val propertiesProvider = context.registerConfigurationListener()
    logger.info { propertiesProvider }

    context.registerSingleObjectToJsonConverterProvider(propertiesProvider)

    try {
      logger.info { "All schema: ${propertiesProvider.get("default").schemaRegistry.findAll()}" }
    } catch (e: Exception) {
      logger.error { "Could not connect to registry." }
    }
  }

  override fun stop(bundleContext: BundleContext) {
    registrations.forEach { it.unregister() }
  }

  /**
   * Register a listener reacting on configuration changes.
   */
  private fun BundleContext.registerConfigurationListener(): AxonAvroSchemaRegistryApicurioPluginPropertiesProvider {
    val configurationListener = AxonAvroSchemaRegistryApicurioPluginConfigurationListener()
    registrations.add(
      registerService(ConfigurationListener::class.java, configurationListener, null)
    )
    return configurationListener
  }

  private fun BundleContext.registerSingleObjectToJsonConverterProvider(propertiesProvider: AxonAvroSchemaRegistryApicurioPluginPropertiesProvider) {
    val provider = ApicurioSingleObjectToJsonConverterProvider(propertiesProvider)
    registrations.add(
      registerService(SingleObjectToJsonConverterProvider::class.java.name, provider, null)
    )
  }

}
