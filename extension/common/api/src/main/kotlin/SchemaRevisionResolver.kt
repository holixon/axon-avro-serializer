package io.holixon.axon.avro.common

import org.apache.avro.Schema
import java.util.*
import java.util.function.Function

/**
 * Returns the revision for a given schema.
 */
interface SchemaRevisionResolver : Function<Schema, Optional<SchemaRevision>>
