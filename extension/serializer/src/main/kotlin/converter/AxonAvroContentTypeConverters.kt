package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ChainingConverter

object AxonAvroContentTypeConverters {

  /**
   * Registers converters for [GenericData.Record] from bytes and back. Will never change the schema.
   */
  fun ChainingConverter.registerGenericDataRecordConverters(registry: AvroSchemaReadOnlyRegistry): ChainingConverter = apply {
    val converter = GenericDataRecordAxonConverter(registry)
    registerConverter(converter.byteArrayToGenericDataRecordConverter)
    registerConverter(converter.genericDataRecordToByteArrayConverter)
  }

}
