package io.holixon.axon.avro.serializer.ext

import io.holixon.axon.avro.common.AvroCommon.propertyBasedSchemaRevisionResolver
import io.holixon.axon.avro.common.AvroSchemaRegistry
import io.holixon.axon.avro.common.SchemaRevision
import io.holixon.axon.avro.common.SchemaRevisionResolver
import io.holixon.axon.avro.common.ext.FunctionExt.invoke
import io.holixon.axon.avro.common.ext.SchemaExt.extractSchemaInfo
import io.holixon.axon.avro.common.type.AvroSchemaInfo
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import io.holixon.axon.avro.serializer.AxonAvroExtension
import org.apache.avro.Schema

/**
 * Utils and extensions for avro's [Schema] class.
 */
object SchemaExt {

  fun Schema.findOrRegister(registry:AvroSchemaRegistry): AvroSchemaWithId = registry.register(this)

  val axonRevisionResolver: SchemaRevisionResolver = propertyBasedSchemaRevisionResolver(AxonAvroExtension.PROP_REVISION)

  val Schema.revision : SchemaRevision?
    get() = axonRevisionResolver(this).orElse(null)

  val Schema.info : AvroSchemaInfo
    get() = extractSchemaInfo(axonRevisionResolver)
}
