package io.holixon.axon.avro.serializer


import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import io.holixon.avro.adapter.api.SchemaId
import io.holixon.avro.adapter.api.SchemaRevision
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

object AxonAvroExtension {
  const val PROP_REVISION = "revision"

  fun avroSchemaInfo(context: String, name: String, revision: SchemaRevision?) = AvroSchemaInfoData(namespace = context, name = name, revision = revision)
  fun avroSchema(id: SchemaId, schema: Schema) = AvroSchemaWithIdData(schemaId = id, schema = schema, revision = schema.revision)

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!

}
