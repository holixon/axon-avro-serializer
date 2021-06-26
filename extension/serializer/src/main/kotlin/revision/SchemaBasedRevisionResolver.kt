package io.holixon.axon.avro.serializer.revision

import io.holixon.axon.avro.serializer.AxonAvroExtension.schemaForClass
import io.holixon.axon.avro.serializer.ext.AbstractRevisionResolver
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision

/**
 * Reads the AxonAvroCoreExtension#PROP_REVISION property from schema derived from
 * payloadType.
 */
open class SchemaBasedRevisionResolver : AbstractRevisionResolver() {

  override fun revisionOf(payloadType: Class<*>): String?  = schemaForClass(payloadType).revision

  fun <T:Any> revisionOf(payload: T) : String? {
    TODO()
  }
}
