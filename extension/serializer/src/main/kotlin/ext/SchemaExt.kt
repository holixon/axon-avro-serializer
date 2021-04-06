package io.holixon.axon.avro.serializer.ext

import io.toolisticon.avro.adapter.api.*
import io.toolisticon.avro.adapter.api.AvroAdapterApi.extractSchemaInfo
import io.toolisticon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.Schema

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  fun Schema.findOrRegister(registry: AvroSchemaRegistry): AvroSchemaWithId = registry.register(this)


  val Schema.revision : SchemaRevision?
    get() = AvroAdapterDefault.schemaRevisionResolver.apply(this).orElse(null)

  val Schema.info : AvroSchemaInfo
    get() = extractSchemaInfo(AvroAdapterDefault.schemaRevisionResolver)
}
