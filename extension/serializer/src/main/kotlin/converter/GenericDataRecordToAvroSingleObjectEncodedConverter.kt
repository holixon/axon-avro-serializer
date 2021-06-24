package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ContentTypeConverter

/**
 * Converts AVRO Generic Data Record into AVRO Single Object Encoded bytes, preserving the writer schema.
 */
class GenericDataRecordToAvroSingleObjectEncodedConverter : ContentTypeConverter<GenericData.Record, AvroSingleObjectEncoded> {

  private val encoder: DefaultGenericDataRecordToSingleObjectEncoder = DefaultGenericDataRecordToSingleObjectEncoder()

  override fun convert(original: GenericData.Record): AvroSingleObjectEncoded {
    return encoder.encode(original)
  }

  override fun expectedSourceType(): Class<GenericData.Record> = GenericData.Record::class.java

  override fun targetType(): Class<AvroSingleObjectEncoded> = AvroSingleObjectEncoded::class.java

}
