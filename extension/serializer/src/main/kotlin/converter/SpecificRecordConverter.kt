package io.holixon.axon.avro.serializer.converter


import io.holixon.extensions.kotlin.AxonAvroKotlinExtensions.contentTypeConverter
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToSingleObjectConverter
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.ContentTypeConverter

/**
 * Can convert Avro generated SpecificRecordBase instances to ByteArray using the built in default layout for Single Objects.
 * Can convert ByteArray to SpecificRecordBase by looking up the encoded schemaId at the schemaRegistry.
 */
class SpecificRecordConverter(
  schemaRegistry: AvroSchemaRegistry
) {

  private val schemaResolver = schemaRegistry.schemaResolver()
  // FIXME: add configuration to this.
  private val converter = DefaultSpecificRecordToSingleObjectConverter(schemaResolver)

  val specificRecordToByteArrayConverter: ContentTypeConverter<SpecificRecordBase, ByteArray> = contentTypeConverter { toByteArray(it) }
  val byteArrayToSpecificRecordConverter: ContentTypeConverter<ByteArray, SpecificRecordBase> = contentTypeConverter { fromByteArray(it) }

  fun toByteArray(data: SpecificRecordBase): ByteArray = converter.encode(data)

  fun fromByteArray(data: ByteArray): SpecificRecordBase = converter.decode(data)
}
