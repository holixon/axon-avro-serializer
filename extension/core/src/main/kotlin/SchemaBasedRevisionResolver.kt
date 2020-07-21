package io.toolisticon.axon.avro

import io.toolisticon.axon.avro.AxonAvroExtension.PROP_REVISION
import io.toolisticon.axon.avro.AxonAvroExtension.schemaForClass
import org.axonframework.serialization.RevisionResolver
import kotlin.reflect.KClass

/**
 * Reads the AxonAvroCoreExtension#PROP_REVISION property from schema derived from
 * payloadType.
 */
class SchemaBasedRevisionResolver : RevisionResolver {

  override fun revisionOf(payloadType: Class<*>): String? {
    val schema = schemaForClass(payloadType)

    return schema.getObjectProp(PROP_REVISION) as String?
  }

  fun revisionOf(payloadType: KClass<*>) = revisionOf(payloadType.java)
}
