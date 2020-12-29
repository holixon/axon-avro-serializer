package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.serializer.AxonAvroExtension.schemaForClass
import io.holixon.axon.avro.schema.api.ext.SchemaExt.revision
import io.holixon.extensions.kotlin.serialization.AbstractRevisionResolver

/**
 * Reads the AxonAvroCoreExtension#PROP_REVISION property from schema derived from
 * payloadType.
 */
class SchemaBasedRevisionResolver : AbstractRevisionResolver() {

  override fun revisionOf(payloadType: Class<*>): String?  = schemaForClass(payloadType).revision

}
