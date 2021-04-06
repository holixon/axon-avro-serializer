package io.holixon.axon.avro.serializer


import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import io.toolisticon.avro.adapter.api.SchemaId
import io.toolisticon.avro.adapter.api.SchemaRevision
import io.toolisticon.avro.adapter.api.type.AvroSchemaInfoData
import io.toolisticon.avro.adapter.api.type.AvroSchemaWithIdData
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

object AxonAvroExtension {
  const val PROP_REVISION = "revision"

  fun avroSchemaInfo(context: String, name: String, revision: SchemaRevision?) = AvroSchemaInfoData(context = context, name = name, revision = revision)
  fun avroSchema(id: SchemaId, schema: Schema) = AvroSchemaWithIdData(id = id, schema = schema, revision = schema.revision)

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!

}
