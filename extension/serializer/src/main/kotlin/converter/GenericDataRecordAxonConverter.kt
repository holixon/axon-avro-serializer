package io.holixon.axon.avro.serializer.converter


import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.common.converter.DefaultGenericDataRecordToSingleObjectConverter
import io.holixon.extensions.kotlin.AxonKotlinExtensions.contentTypeConverter
import org.apache.avro.generic.GenericData
import org.axonframework.serialization.ContentTypeConverter

/**
 * Can convert [GenericData.Record] instances to ByteArray using the built in default layout for Single Objects and back..
 * Can convert ByteArray to [GenericData.Record] by looking up the encoded schemaId at the schemaRegistry.
 *
 * It will NOT change the schema id, but use the same (writer schema).
 */
class GenericDataRecordAxonConverter(
  avroSchemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry
) {

  private val schemaResolver = avroSchemaReadOnlyRegistry.schemaResolver()
  private val converter = DefaultGenericDataRecordToSingleObjectConverter(schemaResolver)

  val genericDataRecordToByteArrayConverter: ContentTypeConverter<GenericData.Record, ByteArray> = contentTypeConverter { toByteArray(it) }
  val byteArrayToGenericDataRecordConverter: ContentTypeConverter<ByteArray, GenericData.Record> =
    contentTypeConverter { fromByteArray(it) }

  fun toByteArray(data: GenericData.Record): ByteArray = converter.encode(data)

  fun fromByteArray(data: ByteArray): GenericData.Record = converter.decode(data)
}
