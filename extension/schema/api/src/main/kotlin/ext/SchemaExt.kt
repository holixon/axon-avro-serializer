package io.holixon.axon.avro.schema.api.ext

import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import kotlin.reflect.KClass

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  val Schema.revision get() = getObjectProp(AxonAvroSchemaApi.PROP_REVISION) as String?

  val Schema.info
    get() = AxonAvroSchemaApi.AxonAvroSchemaInfoData(
      context = namespace,
      name = name,
      revision = revision
    )
}
