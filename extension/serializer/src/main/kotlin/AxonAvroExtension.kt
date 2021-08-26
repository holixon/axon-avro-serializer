package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

/**
 * Helpers for Avro used by the Axon Serializer.
 */
object AxonAvroExtension {
  /**
   * Revision property.
   */
  const val PROP_REVISION = "revision"

  /**
   * Creates Avro Schema Info.
   */
  fun avroSchemaInfo(context: String, name: String, revision: AvroSchemaRevision?) =
    AvroSchemaInfoData(namespace = context, name = name, revision = revision)

  /**
   * Creates Avro Schema with id.
   */
  fun avroSchema(id: AvroSchemaId, schema: Schema) = AvroSchemaWithIdData(schemaId = id, schema = schema, revision = schema.revision)

  /**
   * Reads schema from a record class.
   */
  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  /**
   * Reads schema from a record class.
   */
  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  /**
   * Determines class for serialized type.
   */
  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!

}
