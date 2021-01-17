package io.holixon.axon.avro.common

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import java.util.function.Function

/**
 * Returns a unique id for the given schema that is used to load the schema from a repository.
 *
 * The DEFAULT implementation uses the built-in fingerprint.
 */
interface SchemaIdSupplier : Function<Schema, SchemaId> {
  companion object {
    /**
     * @see [SchemaNormalization.parsingFingerprint64(Schema)]
     */
    val DEFAULT = object : SchemaIdSupplier {
      override fun apply(schema: Schema): SchemaId = SchemaNormalization.parsingFingerprint64(schema)
    }
  }
}
