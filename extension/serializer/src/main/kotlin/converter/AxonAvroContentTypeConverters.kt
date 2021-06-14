package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSchemaRegistry
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ChainingConverter

object AxonAvroContentTypeConverters {

  /**
   * Registers converters for [GenericData.Record] from bytes and back.
   */
  fun ChainingConverter.registerGenericDataRecordConverters(registry: AvroSchemaRegistry): ChainingConverter = apply {
    val converter = GenericDataRecordAxonConverter(registry)
    registerConverter(converter.byteArrayToGenericDataRecordConverter)
    registerConverter(converter.genericDataRecordToByteArrayConverter)
  }

  /**
   * Registers converters for SpecificRecord from bytes and back.
   */
  fun ChainingConverter.registerSpecificRecordConverters(registry: AvroSchemaRegistry): ChainingConverter = apply {
    val converter = SpecificRecordAxonConverter(registry)
    registerConverter(converter.genericDataRecordToSpecificRecordConverter)
    registerConverter(converter.specificRecordToGenericDataRecordConverter)
    registerConverter(converter.specificRecordToSimpleObjectEncoded)
    registerConverter(converter.simpleObjectEncodedToSpecificRecordConverter)
  }
}
