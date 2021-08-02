package io.holixon.axon.avro.serializer.plugin.ext

import io.holixon.axon.avro.registry.plugin.SingleObjectToJsonConverterProvider
import io.holixon.axon.avro.serializer.plugin.AxonAvroSerializerPlugin
import org.osgi.framework.BundleContext

/**
 * Find existing implementations of the SPI to access the schema-based JSON converters and return one.
 */
fun BundleContext.findSchemaRegistryProvider(): SingleObjectToJsonConverterProvider {
  return try {
    val serviceCandidates = getServiceReferences(SingleObjectToJsonConverterProvider::class.java, null)
    val serviceCandidate = when (serviceCandidates.size) {
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
    val provider: SingleObjectToJsonConverterProvider = getService(serviceCandidate)
    AxonAvroSerializerPlugin.logger.info { "Using $provider" }
    provider
  } catch (e: Exception) {
    throw IllegalArgumentException("Error initializing Avro Schema Registry", e)
  }
}
