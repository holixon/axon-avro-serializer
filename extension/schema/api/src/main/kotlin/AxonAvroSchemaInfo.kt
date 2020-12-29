package io.holixon.axon.avro.schema.api


/**
 * The schema info provides the relevant identifiers for a schema:
 *
 * * context (aka namespace)
 * * name
 * * revision
 */
interface AxonAvroSchemaInfo {

  val context: String

  val name: String

  val revision: String?

  val canonicalName : String
    get() = "$context.$name"
}
