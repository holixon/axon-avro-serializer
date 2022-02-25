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

  /**
   * Revision of the schema of the payload.
   */
  fun revisionOf(payloadType: KClass<*>): String? = revisionOf(payloadType.java)

  /**
   * Revision of the schema of the payload.
   */
  override fun revisionOf(payloadType: Class<*>): String?  = schemaForClass(payloadType).revision

  /**
   * Extract revision of generic record.
   */
  fun revisionOf(record: GenericData.Record): String? = record.schema.revision

  /**
   * Extract revision of specific record.
   */
  fun revisionOf(record: SpecificRecordBase): String? = record.schema.revision
}
