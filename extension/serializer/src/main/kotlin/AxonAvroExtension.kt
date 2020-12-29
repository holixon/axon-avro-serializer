package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.schema.api.ext.SchemaExt.revision
import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.RevisionResolver
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

object AxonAvroExtension {

  const val MAGIC_BYTE: Int = 0x0

  /**
   * Reads the AxonAvroCoreExtension#PROP_REVISION from schema derived from
   * payloadType.
   */
  @JvmStatic
  val schemaBasedRevisionResolver = object : RevisionResolver {
    override fun revisionOf(payloadType: Class<*>): String? = schemaForClass(payloadType).revision
    fun revisionOf(payloadType: KClass<*>) = revisionOf(payloadType.java)
  }

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!

}
