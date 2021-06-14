package io.holixon.axon.avro.serializer.ext

import io.holixon.avro.adapter.api.AvroAdapterApi.extractSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.Schema

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  fun Schema.find(schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry): AvroSchemaWithId =
    schemaReadOnlyRegistry.findByInfo(this.info).orElseThrow { IllegalArgumentException("Could not resolve schema $this") }

  val Schema.revision: AvroSchemaRevision?
    get() = AvroAdapterDefault.schemaRevisionResolver.apply(this).orElse(null)

  val Schema.info: AvroSchemaInfo
    get() = extractSchemaInfo(AvroAdapterDefault.schemaRevisionResolver)
}
