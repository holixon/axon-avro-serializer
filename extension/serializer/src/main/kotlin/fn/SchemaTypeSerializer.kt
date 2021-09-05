package io.holixon.axon.avro.serializer.fn

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import io.holixon.avro.adapter.common.encoder.DefaultSpecificRecordToSingleObjectEncoder
import io.holixon.axon.avro.serializer.ext.TypeExt.isAvroSingleObjectEncoded
import io.holixon.axon.avro.serializer.type.AvroSchemaSerializedObject
import io.holixon.axon.avro.serializer.type.AvroSchemaSingleObjectSerializedObject
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.Converter
import org.axonframework.serialization.SerializationException
import kotlin.reflect.KClass

/**
 * Schema type serializer.
 */
class SchemaTypeSerializer<S : Any>(
  /**
   * The type specific encoder function.
   */
  private val encoder: (S) -> AvroSingleObjectEncoded,
  /**
   * How to get the schema from type.
   */
  private val schemaAccessor: (S) -> Schema,
  /**
   * The axon converter (chain).
   */
  private val converter: Converter
) {

  companion object {
    private val genericRecordEncoder = DefaultGenericDataRecordToSingleObjectEncoder()
    private val specificRecordEncoder = DefaultSpecificRecordToSingleObjectEncoder()

    /**
     * Serializer for generic record, using the specified converter.
     */
    fun genericRecordDataSerializer(converter: Converter) = SchemaTypeSerializer(
      encoder = { genericRecordEncoder.encode(it) },
      schemaAccessor = GenericData.Record::getSchema,
      converter = converter
    )

    /**
     * Serializer for specific record, using the specified converter.
     */
    fun specificRecordDataSerializer(converter: Converter) = SchemaTypeSerializer(
      encoder = { specificRecordEncoder.encode(it) },
      schemaAccessor = SpecificRecordBase::getSchema,
      converter = converter
    )
  }

  /**
   * Serializes data into single object encoded format.
   */
  fun serializeToSingleObject(data: S): AvroSchemaSerializedObject<AvroSingleObjectEncoded> = AvroSchemaSingleObjectSerializedObject(
    bytes = encoder(data),
    schema = schemaAccessor(data)
  )

  /**
   * Serializes data into expected representation.
   */
  fun <T : Any> serialize(data: S, expectedRepresentation: KClass<T>): AvroSchemaSerializedObject<T> =
    serialize(data, expectedRepresentation.java)

  /**
   * Serializes data into expected representation.
   */
  @Suppress("UNCHECKED_CAST")
  fun <T : Any> serialize(data: S, expectedRepresentation: Class<T>): AvroSchemaSerializedObject<T> = try {
    // this is a shortcut to avoid convert path lookup when the output is ByteArray anyway
    if (expectedRepresentation.isAvroSingleObjectEncoded()) {
      serializeToSingleObject(data) as AvroSchemaSerializedObject<T>
    } else {
      val schema: Schema = schemaAccessor(data)
      val singleObjectBytes: AvroSingleObjectEncoded = encoder(data)

      val content: T = converter.convert(singleObjectBytes, expectedRepresentation)

      AvroSchemaSerializedObject(content, expectedRepresentation, schema)
    }
  } catch (e: Exception) {
    throw SerializationException("Unable to serialize object into $expectedRepresentation", e)
  }
}
