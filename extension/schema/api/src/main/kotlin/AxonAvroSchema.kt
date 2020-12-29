package io.holixon.axon.avro.schema.api

import org.apache.avro.Schema

interface AxonAvroSchema  : AxonAvroSchemaInfo {

  val globalId: Long

  val schema: Schema
}
