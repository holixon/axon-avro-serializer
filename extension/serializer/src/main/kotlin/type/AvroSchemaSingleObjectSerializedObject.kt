package io.holixon.axon.avro.serializer.type

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.Schema

/**
 * Avro Schema Single Object Serialized object.
 */
class AvroSchemaSingleObjectSerializedObject(bytes: AvroSingleObjectEncoded, serializedType: AvroSchemaSerializedType) :
  AvroSchemaSerializedObject<AvroSingleObjectEncoded>(bytes, AvroSingleObjectEncoded::class.java, serializedType) {

  constructor(bytes: AvroSingleObjectEncoded, schema: Schema) : this(bytes, AvroSchemaSerializedType(schema))

}
