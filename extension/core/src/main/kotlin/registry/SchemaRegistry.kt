package io.toolisticon.axon.avro.registry

import io.toolisticon.axon.avro.SchemaBasedRevisionResolver
import org.apache.avro.specific.SpecificRecord

class SchemaRegistry(
  private val schemaStore: SchemaStore,
  private val schemaBasedRevisionResolver: SchemaBasedRevisionResolver
) {

  fun <T : SpecificRecord> register(id: Int, recordClass: Class<T>) {
    schemaStore.add(SchemaSerializedType.create(
      id = id,
      recordClass = recordClass,
      revision = schemaBasedRevisionResolver.revisionOf(recordClass) ?: "0"
    ))
  }
}
