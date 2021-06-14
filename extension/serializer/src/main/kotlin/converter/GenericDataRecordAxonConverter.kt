package io.holixon.axon.avro.serializer.converter


import io.holixon.extensions.kotlin.AxonAvroKotlinExtensions.contentTypeConverter
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.common.converter.DefaultGenericDataRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToSingleObjectConverter
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.ContentTypeConverter

/**
 * Can convert [GenericData.Record] instances to ByteArray using the built in default layout for Single Objects.
 * Can convert ByteArray to [GenericData.Record] by looking up the encoded schemaId at the schemaRegistry.
 */
class GenericDataRecordAxonConverter(
  schemaRegistry: AvroSchemaRegistry
) {

  private val schemaResolver = schemaRegistry.schemaResolver()
  // FIXME: add configuration to this.
  private val converter = DefaultGenericDataRecordToSingleObjectConverter(schemaResolver)

  val genericDataRecordToByteArrayConverter: ContentTypeConverter<GenericData.Record, ByteArray> = contentTypeConverter { toByteArray(it) }
  val byteArrayToGenericDataRecordConverter: ContentTypeConverter<ByteArray, GenericData.Record> = contentTypeConverter { fromByteArray(it) }

  fun toByteArray(data: GenericData.Record): ByteArray = converter.encode(data)

  fun fromByteArray(data: ByteArray): GenericData.Record = converter.decode(data)
}
