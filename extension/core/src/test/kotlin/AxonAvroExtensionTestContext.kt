package io.toolisticon.axon.avro

import io.toolisticon.axon.avro.registry.SchemaRegistry
import io.toolisticon.axon.avro.registry.SchemaSerializedType
import io.toolisticon.axon.avro.registry.memory.InMemorySchemaStore
import test.fixture.SampleEvent

internal object AxonAvroExtensionTestContext {

  val schemaBasedRevisionResolver = SchemaBasedRevisionResolver()

  val events = mapOf(SampleEvent::class to SchemaSerializedType.create(
      id = 1,
      recordClass = SampleEvent::class.java,
      revision = schemaBasedRevisionResolver.revisionOf(SampleEvent::class.java) ?: "0"
    )
  )

  val schemaProvider = InMemorySchemaStore()
  val schemaRegistry = SchemaRegistry(schemaProvider, schemaBasedRevisionResolver)

  fun resourceAsString(path: String) : String = object {}.javaClass.getResource(path).readText()

  init {
    events.values.forEach { schemaProvider.add(it) }
  }

}
