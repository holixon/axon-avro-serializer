package io.holixon.axon.avro.serializer.plugin

import io.axoniq.axonserver.plugin.interceptor.ReadEventInterceptor
import io.holixon.axon.avro.registry.plugin.SingleObjectToJsonConverterProvider
import io.holixon.axon.avro.serializer.plugin.ext.findSchemaRegistryProvider
import mu.KLogging
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration

/**
 * Plugin responsible for de-serialization of AVRO messages.
 */
class AxonAvroSerializerPlugin : BundleActivator {

  companion object : KLogging()
  private val registrations = mutableSetOf<ServiceRegistration<*>>()

  override fun start(context: BundleContext) {
    // find a schema resolver provider
    val schemaRegistryProvider = context.findSchemaRegistryProvider()
    context.registerReadEventInterceptor(schemaRegistryProvider)
  }

  override fun stop(context: BundleContext) {
    registrations.forEach { it.unregister() }
  }

  private fun BundleContext.registerReadEventInterceptor(schemaResolverProvider: SingleObjectToJsonConverterProvider) =
    AvroSingleObjectEncodedToJsonReadEventInterceptor(schemaResolverProvider).also {
      registrations.add(
        registerService(ReadEventInterceptor::class.java, it, null)
      )
    }

}
