package io.holixon.axon.avro.common

import io.holixon.axon.avro.common.type.AvroSchemaInfo
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import org.apache.avro.Schema
import java.util.*

interface AvroSchemaRegistry {

  fun register(schema: Schema): AvroSchemaWithId

  fun findById(globalId: Long): Optional<AvroSchemaWithId>

  fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId>

  fun findByContextAndName(context: String, name: String): List<AvroSchemaWithId>

  fun findAll() : List<AvroSchemaWithId>
}
