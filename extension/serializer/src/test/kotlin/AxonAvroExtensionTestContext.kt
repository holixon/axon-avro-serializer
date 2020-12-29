package io.holixon.axon.avro

import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi
import io.holixon.axon.avro.serializer.SchemaBasedRevisionResolver
import io.holixon.axon.avro.serializer.SchemaSerializedType
import test.fixture.SampleEvent

internal object AxonAvroExtensionTestContext {
//
//  val schemaBasedRevisionResolver = SchemaBasedRevisionResolver()
//
//  val events = mapOf(SampleEvent::class to SchemaSerializedType.create(
//      id = 1,
//      recordClass = SampleEvent::class.java,
//      revision = schemaBasedRevisionResolver.revisionOf(SampleEvent::class.java) ?: "0"
//    )
//  )
//
//  val schemaProvider = AxonAvroSchemaApi.InMemoryAxonAvroSchemaRegistry()
//  val schemaRegistry = SchemaRegistry(schemaProvider, schemaBasedRevisionResolver)
//
//  fun resourceAsString(path: String) : String = object {}.javaClass.getResource(path).readText()
//
//  init {
//    events.values.forEach { schemaProvider.add(it) }
//  }

}
