package io.holixon.axon.avro.serializer.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.common.converter.DefaultGenericDataRecordToSingleObjectConverter
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ContentTypeConverter

/**
 * Type converter to convert AVRO Single Object Encoded bytes into AVRO Generic Data Record, preserving the writer schema.
 */
class AvroSingleObjectEncodedToGenericDataRecordTypeConverter(
  schemaResolver: SchemaResolver
) : ContentTypeConverter<AvroSingleObjectEncoded, GenericData.Record> {

  private val converter: DefaultGenericDataRecordToSingleObjectConverter = DefaultGenericDataRecordToSingleObjectConverter(schemaResolver)

  override fun convert(original: AvroSingleObjectEncoded): GenericData.Record {
    return converter.decode(original)
  }

  override fun expectedSourceType(): Class<AvroSingleObjectEncoded> = AvroSingleObjectEncoded::class.java

  override fun targetType(): Class<GenericData.Record> = GenericData.Record::class.java

}
