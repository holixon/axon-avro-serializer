package io.holixon.axon.avro.serializer

import mu.KLogging
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroSerializerTest {

  companion object : KLogging()

  val event = SampleEvent.newBuilder()
    .setValue("foo")
    .build()


  @Test
  internal fun name() {
    logger.info { "event: $event" }
    logger.info { "schema: ${event.schema}" }
  }
}

