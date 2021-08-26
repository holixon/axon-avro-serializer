package io.holixon.axon.avro.serializer.ext

import io.holixon.avro.adapter.api.encoder.GenericDataRecordToSingleObjectEncoder
import io.holixon.avro.adapter.api.encoder.SpecificRecordToSingleObjectEncoder
import io.holixon.axon.avro.serializer.type.AvroSchemaSingleObjectSerializedObject
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Helpers for Avro Encoding.
 */
object AvroEncodingExt {

  /**
   * Create serialized object of provided data using specific record format.
   */
  fun SpecificRecordToSingleObjectEncoder.serializedObject(data: SpecificRecordBase) = AvroSchemaSingleObjectSerializedObject(
    encode(data),
    data.schema
  )

  /**
   * Create serialized object of provided data using generic record format.
   */
  fun GenericDataRecordToSingleObjectEncoder.serializedObject(data: GenericData.Record) = AvroSchemaSingleObjectSerializedObject(
    encode(data),
    data.schema
  )
}
