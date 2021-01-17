package io.holixon.axon.avro.common.ext

import io.holixon.axon.avro.common.AvroCommon
import io.holixon.axon.avro.common.SchemaRevision
import io.holixon.axon.avro.common.SchemaRevisionResolver
import io.holixon.axon.avro.common.ext.FunctionExt.invoke
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.specific.SpecificData
import kotlin.reflect.KClass

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>): Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  val Schema.fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

  @JvmStatic
  fun Schema.extractSchemaInfo(schemaRevisionResolver: SchemaRevisionResolver) = AvroCommon.AvroSchemaInfoData(
    context = namespace,
    name = name,
    revision = schemaRevisionResolver(this).orElse(null)
  )


}
