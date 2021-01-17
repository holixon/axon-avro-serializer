package io.holixon.axon.avro.common

import io.holixon.axon.avro.common.type.AvroSchemaWithId
import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore
import java.util.*
import java.util.function.Function

interface SchemaResolver : Function<SchemaId, Optional<AvroSchemaWithId>>, SchemaStore {

  override fun findByFingerprint(fingerprint: Long): Schema? = apply(fingerprint).map { it.schema }.orElse(null)

}
