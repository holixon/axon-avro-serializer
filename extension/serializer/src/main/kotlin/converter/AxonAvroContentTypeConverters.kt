package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSchemaRegistry
import org.axonframework.serialization.ChainingConverter

object AxonAvroContentTypeConverters {

  /**
   * Registers converters for SpecificRecord from bytes and back.
   */
  fun ChainingConverter.registerSpecificRecordConverters(registry: AvroSchemaRegistry): ChainingConverter = apply {
    val converter = SpecificRecordConverter(registry)
    registerConverter(converter.byteArrayToSpecificRecordConverter)
    registerConverter(converter.specificRecordToByteArrayConverter)
  }
}
