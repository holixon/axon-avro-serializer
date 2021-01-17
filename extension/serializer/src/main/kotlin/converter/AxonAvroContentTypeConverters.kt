package io.holixon.axon.avro.serializer.converter

import io.holixon.axon.avro.common.AvroSchemaRegistry
import org.axonframework.serialization.ChainingConverter

object AxonAvroContentTypeConverters {

  fun ChainingConverter.registerSpecificRecordConverters(registry: AvroSchemaRegistry): ChainingConverter = apply {
    val converter = SpecificRecordConverter(registry)
    registerConverter(converter.byteArrayToSpecificRecordConverter)
    registerConverter(converter.specificRecordToByteArrayConverter)
  }

}
