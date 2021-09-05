package io.holixon.axon.avro.serializer.revision

import io.holixon.axon.avro.serializer.AxonAvroExtension.schemaForClass
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.axonframework.serialization.RevisionResolver
import kotlin.reflect.KClass

/**
 * Reads the AxonAvroCoreExtension#PROP_REVISION property from schema derived from
 * payloadType.
 */
open class SchemaBasedRevisionResolver : RevisionResolver {

  fun revisionOf(payloadType: KClass<*>): String? = revisionOf(payloadType.java)

  override fun revisionOf(payloadType: Class<*>): String?  = schemaForClass(payloadType).revision

  fun revisionOf(record: GenericData.Record): String? = record.schema.revision
  fun revisionOf(record: SpecificRecordBase): String? = record.schema.revision
}
