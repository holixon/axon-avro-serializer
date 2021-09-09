package io.holixon.axon.avro.serializer.metadata

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.common.registry.CompositeAvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.axon.avro.serializer.type.AvroSchemaSerializedType
import io.holixon.axon.avro.serializer.type.AvroSchemaSingleObjectSerializedObject
import org.apache.avro.Schema
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.SerializedObject

/**
 * For serialization of [MetaData] we use the generated [AvroMetaData].
 * Here we implement helper functions dealing with those two classes.
 */
object AvroMetaDataExt {
  /**
   * The [Schema] of the generated [AvroMetaData].
   */
  val METADATA_SCHEMA: Schema = AvroMetaData.getClassSchema()

  /**
   * [AvroSchemaWithId] of the generated [AvroMetaData].
   */
  val SCHEMA_WITH_ID: AvroSchemaWithId = METADATA_SCHEMA.avroSchemaWithId
  private val SERIALIZED_TYPE: AvroSchemaSerializedType = AvroSchemaSerializedType(SCHEMA_WITH_ID)

  /**
   * A [AvroSchemaReadOnlyRegistry] that only contains a single entry for [AvroMetaData].
   */
  @JvmField
  val metaDataSchemaRegistry: AvroSchemaReadOnlyRegistry = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(METADATA_SCHEMA)

  /**
   * Convert MetaData from specific record to axon default.
   */
  @JvmStatic
  fun convertFromAvro(avroMetaData: AvroMetaData): MetaData = MetaData.from(avroMetaData.values)

  /**
   * Convert axon default metaData to specific record.
   */
  @JvmStatic
  fun convertToAvro(metaData: MetaData): AvroMetaData = AvroMetaData(metaData)

  /**
   * Creates a combined registry of the given registry and the [AvroMetaDataExt.metaDataSchemaRegistry].
   */
  @JvmStatic
  fun compositeSchemaRegistry(registry: AvroSchemaReadOnlyRegistry) = CompositeAvroSchemaReadOnlyRegistry(metaDataSchemaRegistry, registry)

  /**
   * Converts from serialized object to avro serialized object.
   */
  fun toAvroMetaDataObject(serializedObject: SerializedObject<*>): AvroSchemaSingleObjectSerializedObject {
    return AvroSchemaSingleObjectSerializedObject(serializedObject.data as AvroSingleObjectEncoded, SERIALIZED_TYPE)
  }
}
