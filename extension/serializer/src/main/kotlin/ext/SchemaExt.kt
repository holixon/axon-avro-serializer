package io.holixon.axon.avro.serializer.ext

import io.holixon.avro.adapter.api.AvroAdapterApi.extractSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  /**
   * Retrieves schema revision.
   */
  val Schema.revision: AvroSchemaRevision?
    get() = AvroAdapterDefault.schemaRevisionResolver.apply(this).orElse(null)

  /**
   * Retrieves schema info.
   */
  val Schema.info: AvroSchemaInfo
    get() = extractSchemaInfo(AvroAdapterDefault.schemaRevisionResolver)

}
