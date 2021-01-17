package io.holixon.axon.avro.common.type

import io.holixon.axon.avro.common.SchemaId
import org.apache.avro.Schema

/**
 * Tuple wrapping the schema and its id.
 */
interface AvroSchemaWithId : AvroSchemaInfo {

  val id: SchemaId

  val schema: Schema

}
