package io.toolisticon.axon.avro

import org.apache.avro.specific.SpecificData
import org.apache.avro.util.ClassUtils
import org.axonframework.serialization.SerializedType
import kotlin.reflect.KClass

object AxonAvroExtension {
  /**
   * Name of the additional AVRO schema property that contains the event revision.
   */
  const val PROP_REVISION = "revision"

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>) = SpecificData(recordClass.classLoader).getSchema(recordClass)!!

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun classForType(serializedType: SerializedType) = ClassUtils.forName(serializedType.name)!!
}
