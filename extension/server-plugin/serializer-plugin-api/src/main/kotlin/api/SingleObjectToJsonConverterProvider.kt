package io.holixon.axon.avro.serializer.plugin.api

import io.holixon.avro.adapter.api.converter.SingleObjectToJsonConverter

/**
 * Provider for SingleObjectToJsonConverter.
 */
interface SingleObjectToJsonConverterProvider {
  /**
   * Retrieves a converter for given context.
   * @param contextName name of the context.
   */
  fun get(contextName: ContextName): SingleObjectToJsonConverter
}

/**
 * Context name.
 */
typealias ContextName = String

/**
 * Registry name.
 */
const val PROPERTY_CONVERTER_NAME = "registry-name"
