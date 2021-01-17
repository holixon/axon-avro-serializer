package io.holixon.axon.avro.common.ext

import io.holixon.axon.avro.common.AvroSchemaRegistry
import io.holixon.axon.avro.common.SchemaId
import io.holixon.axon.avro.common.SchemaResolver
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import java.util.*

object AvroSchemaRegistryExt {

  fun AvroSchemaRegistry.schemaResolver() = object : SchemaResolver {
    override fun apply(schemaId: SchemaId): Optional<AvroSchemaWithId> = this@schemaResolver.findById(schemaId)
  }

}
