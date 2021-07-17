package io.holixon.axon.avro.serializer.plugin

import io.axoniq.axonserver.plugin.ConfigurationListener
import io.axoniq.axonserver.plugin.interceptor.ReadEventInterceptor
import io.holixon.axon.avro.serializer.plugin.properties.AxonAvroSerializerPluginConfigurationListener
import io.holixon.axon.avro.serializer.plugin.properties.AxonAvroSerializerPluginPropertiesProvider
import mu.KLogging
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration

typealias ContextName = String

class AxonAvroSerializerPlugin : BundleActivator {
  companion object : KLogging() {
    const val PLUGIN_NAME = "avroSerializerPlugin"
    const val PRINCIPAL_EVENT_HANDLER = "<anonymous>"
  }

  private val registrations = mutableSetOf<ServiceRegistration<*>>()

  override fun start(context: BundleContext) {
    logger.info("STARTING $PLUGIN_NAME")
    logger.info { "registrations: size=${registrations.size}" }

    // make the properties available to Axon Server by registering it as a ConfigurationListener
    val propertiesProvider = context.registerConfigurationListener()
    logger.info { "configurations: $propertiesProvider" }

    val readEventInterceptor = context.registerReadEventInterceptor(propertiesProvider)

    logger.info { "registrations: size=${registrations.size}" }
    logger.info { "registrations: $registrations" }

    try {
      logger.info { "all schema: ${propertiesProvider.get("default").apicurioSchemaRegistry.findAll()}" }
    } catch (e: Exception) {
      logger.error { "could not connect to registry" }
    }
  }

  override fun stop(context: BundleContext) {
    logger.info("STOPPING $PLUGIN_NAME")
    registrations.forEach{ it.unregister() }

    logger.info { "registrations: size=${registrations.size}" }
    logger.info { "registrations: $registrations" }
  }

  private fun BundleContext.registerConfigurationListener(): AxonAvroSerializerPluginPropertiesProvider {
    val configurationListener = AxonAvroSerializerPluginConfigurationListener()

    registrations.add(
      registerService(ConfigurationListener::class.java, configurationListener, null)
    )

    return configurationListener
  }

  private fun BundleContext.registerReadEventInterceptor(propertiesProvider: AxonAvroSerializerPluginPropertiesProvider): AvroSingleObjectEncodedToJsonReadEventInterceptor {
    val interceptor = AvroSingleObjectEncodedToJsonReadEventInterceptor(propertiesProvider)

    registrations.add(
      registerService(ReadEventInterceptor::class.java, interceptor, null)
    )

    return interceptor
  }
}
