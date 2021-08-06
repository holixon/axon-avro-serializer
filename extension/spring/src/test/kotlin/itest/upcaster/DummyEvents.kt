package io.holixon.axon.avro.serializer.spring.itest.upcaster

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToSpecificRecordDecoder
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import upcaster.itest.DummyEvent

object DummyEvents {

  val SCHEMA_EVENT_01 : Schema = Schema.Parser().parse(
    """
      {
        "type": "record",
        "namespace": "upcaster.itest",
        "name": "DummyEvent",
        "revision": "1",
        "fields": [
          {
            "name": "value01",
            "type": {
              "type": "string",
              "avro.java.string": "String"
            }
          }
        ]
      }
    """.trimIndent()
  )

  val SCHEMA_EVENT_10 : Schema = DummyEvent.getClassSchema()

  val registry = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(SCHEMA_EVENT_01, SCHEMA_EVENT_10)
}

internal class DummyEventsTest {

  private val encoder = DefaultGenericDataRecordToSingleObjectEncoder()
  private val decoder = DefaultSingleObjectToSpecificRecordDecoder(DummyEvents.registry.schemaResolver())

  @Test
  internal fun `schema 01 and 10 are incompatibly`() {
    val record = GenericData.Record(DummyEvents.SCHEMA_EVENT_01).apply {
      put("value01", "foo")
    }

    assertThatThrownBy { decoder.decode<DummyEvent>(encoder.encode(record)) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessageContaining("[READER_FIELD_MISSING_DEFAULT_VALUE]")

  }
}
