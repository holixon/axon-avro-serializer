package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.common.AvroCommon.AvroSchemaInfoData
import io.holixon.axon.avro.common.AvroCommon.AvroSchemaWithIdData
import io.holixon.axon.avro.common.AvroCommon.InMemoryAvroSchemaRegistry
import io.holixon.axon.avro.common.SchemaId
import io.holixon.axon.avro.common.SchemaIdSupplier
import io.holixon.axon.avro.common.SchemaRevision
import io.holixon.axon.avro.serializer.ext.SchemaExt
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

object AxonAvroExtension {
  const val PROP_REVISION = "revision"

  fun avroSchemaInfo(context: String, name: String, revision: SchemaRevision?) = AvroSchemaInfoData(context = context, name = name, revision = revision)
  fun avroSchema(id: SchemaId, schema: Schema) = AvroSchemaWithIdData(id = id, schema = schema, revision = schema.revision)

  fun defaultInMemorySchemaRegistry() = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = SchemaIdSupplier.DEFAULT,
    schemaRevisionResolver = SchemaExt.axonRevisionResolver
  )

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!

}
