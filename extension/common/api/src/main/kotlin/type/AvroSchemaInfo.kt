package io.holixon.axon.avro.common.type

import io.holixon.axon.avro.common.SchemaRevision


/**
 * The schema info provides the relevant identifiers for a schema:
 *
 * * context (aka namespace)
 * * name
 * * revision
 */
interface AvroSchemaInfo {

  val context: String

  val name: String

  val revision: SchemaRevision?

  val canonicalName : String
    get() = "$context.$name"
}
