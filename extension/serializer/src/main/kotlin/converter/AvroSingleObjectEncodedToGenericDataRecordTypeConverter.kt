package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToGenericDataRecordDecoder
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ContentTypeConverter

/**
 * Type converter to convert AVRO Single Object Encoded bytes into AVRO Generic Data Record, preserving the writer schema.
 */
class AvroSingleObjectEncodedToGenericDataRecordTypeConverter(
  schemaResolver: AvroSchemaResolver
) : ContentTypeConverter<AvroSingleObjectEncoded, GenericData.Record> {

  private val decoder: DefaultSingleObjectToGenericDataRecordDecoder = DefaultSingleObjectToGenericDataRecordDecoder(schemaResolver)

  override fun convert(original: AvroSingleObjectEncoded): GenericData.Record {
    return decoder.decode(original)
  }

  override fun expectedSourceType(): Class<AvroSingleObjectEncoded> = AvroSingleObjectEncoded::class.java

  override fun targetType(): Class<GenericData.Record> = GenericData.Record::class.java

}
