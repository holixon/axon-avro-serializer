package io.holixon.axon.avro.serializer.plugin.ext

import io.holixon.avro.adapter.api.converter.SingleObjectToJsonConverter
import io.holixon.axon.avro.serializer.plugin.AxonAvroSerializerPlugin
import io.holixon.axon.avro.serializer.plugin.api.ContextName
import io.holixon.axon.avro.serializer.plugin.api.SingleObjectToJsonConverterProvider
import org.osgi.framework.BundleContext
import org.osgi.framework.InvalidSyntaxException
import org.osgi.framework.ServiceReference

/**
 * Find existing implementations of the SPI to access the schema-based JSON converters and return one.
 */
fun BundleContext.findSchemaRegistryProvider(): ServiceReference<SingleObjectToJsonConverterProvider> {
  val serviceCandidates = try {
    getServiceReferences(SingleObjectToJsonConverterProvider::class.java, null)
  } catch (e: InvalidSyntaxException) {
    throw IllegalArgumentException("Error initializing Avro Schema Registry", e)
  } catch (e: IllegalStateException) {
    throw IllegalArgumentException("Error initializing Avro Schema Registry", e)
  }
  return when (serviceCandidates.size) {
    0 -> throw IllegalArgumentException("Could not find any Avro Registry Provider, please install and configure at least one.")
    1 -> serviceCandidates.first()
    else -> {
      // TODO: provide configuration to select one
      AxonAvroSerializerPlugin.logger.warn { "More than one Avro Registry Provider found, taking the first one." }
      serviceCandidates.forEach {
        AxonAvroSerializerPlugin.logger.warn {
          "Provider bundle: ${it.bundle}, provider class: ${it.javaClass.name}"
        }
      }
      serviceCandidates.first()
    }
  }
}

/**
 * Creates a service from candidate.
 */
fun BundleContext.getSchemaRegistryProvider(serviceReference: ServiceReference<SingleObjectToJsonConverterProvider>): SingleObjectToJsonConverterProvider {
  return getService(serviceReference).also {
    AxonAvroSerializerPlugin.logger.info { "Using $it" }
  }
}

/**
 * Release a service.
 */
fun BundleContext.releaseSchemaRegistryProvider(serviceReference: ServiceReference<SingleObjectToJsonConverterProvider>) {
  ungetService(serviceReference)
}

/**
 * Operation run using single object JSON converter.
 */
typealias SingleObjectConverterOperation<T> = (SingleObjectToJsonConverter) -> T

/**
 * Scoped execution of the operation using the SingleObjectJsonConverter provided by the service in given Axon Context.
 */
inline fun <reified T : Any> BundleContext.usingSingleObjectJsonConverterInContext(
  contextName: ContextName,
  serviceReference: ServiceReference<SingleObjectToJsonConverterProvider>,
  serviceWorker: SingleObjectConverterOperation<T>
): T {
  try {
    val provider = getSchemaRegistryProvider(serviceReference)
    return serviceWorker.invoke(provider.get(contextName).also {
      AxonAvroSerializerPlugin.logger.info { "Converter received: $it" }
    })
  } finally {
    releaseSchemaRegistryProvider(serviceReference)
  }
}
