package io.holixon.axon.avro.schema.api

import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore
import java.util.*

interface AxonAvroSchemaRegistry : SchemaStore {

  fun register(schema: Schema) : AxonAvroSchema

  fun findById(globalId: Long) : Optional<AxonAvroSchema>

  fun findByInfo(info: AxonAvroSchemaInfo) : Optional<AxonAvroSchema>

  fun findByContextAndName(context:String, name:String) : List<AxonAvroSchema>

  fun findByInfoOrRegister(info: AxonAvroSchemaInfo, schema: Schema) : AxonAvroSchema = findByInfo(info).orElseGet { register(schema) }

  override fun findByFingerprint(fingerprint: Long): Schema = findById(fingerprint).map { it.schema }.orElseThrow()
}
