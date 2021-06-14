package io.holixon.axon.avro.serializer.converter


import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToGenericDataRecordConverter
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToSingleObjectConverter
import io.holixon.extensions.kotlin.AxonAvroKotlinExtensions.contentTypeConverter
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.ContentTypeConverter

/**
 * Can convert Avro generated SpecificRecordBase instances to [GenericData.Record] using the built in default layout for Single Objects.
 * Can convert [GenericData.Record] to SpecificRecordBase by looking up the encoded schemaId at the schemaRegistry.
 */
class SpecificRecordAxonConverter(
  schemaRegistry: AvroSchemaRegistry
) {

  private val schemaResolver = schemaRegistry.schemaResolver()

  // FIXME: add configuration to this.
  private val recordConverter = DefaultSpecificRecordToGenericDataRecordConverter(schemaResolver)
  private val byteConverter = DefaultSpecificRecordToSingleObjectConverter(schemaResolver)

  val genericDataRecordToSpecificRecordConverter: ContentTypeConverter<GenericData.Record, SpecificRecordBase> =
    contentTypeConverter { fromGenericDataRecord(it) }
  val specificRecordToGenericDataRecordConverter: ContentTypeConverter<SpecificRecordBase, GenericData.Record> =
    contentTypeConverter { toGenericDataRecord(it) }

  val simpleObjectEncodedToSpecificRecordConverter: ContentTypeConverter<AvroSingleObjectEncoded, SpecificRecordBase> =
    contentTypeConverter { fromByteArray(it) }
  val specificRecordToSimpleObjectEncoded: ContentTypeConverter<SpecificRecordBase, AvroSingleObjectEncoded> =
    contentTypeConverter { toByteArray(it) }

  fun fromGenericDataRecord(record: GenericData.Record): SpecificRecordBase = recordConverter.decode(record)

  fun toGenericDataRecord(data: SpecificRecordBase): GenericData.Record = recordConverter.encode(data)

  fun fromByteArray(bytes: AvroSingleObjectEncoded): SpecificRecordBase = byteConverter.decode(bytes)

  fun toByteArray(data: SpecificRecordBase): AvroSingleObjectEncoded = byteConverter.encode(data)
}
