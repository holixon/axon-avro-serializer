package io.holixon.axon.avro.serializer.revision

import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class SchemaBasedRevisionResolverTest {

  private val resolver = SchemaBasedRevisionResolver()

  @Test
  internal fun `read revision tag from schema`() {
    assertThat(resolver.revisionOf(SampleEvent::class))
      .isEqualTo("4711")
  }

  @Test
  internal fun `read revision from generic record`() {
    val record = GenericData.Record(SampleEvent.`SCHEMA$`).apply {
      put("value", "foo")
    }

    assertThat(resolver.revisionOf(record)).isEqualTo("4711")
  }

  @Test
  internal fun `read revision from specific record`() {
    val record = SampleEvent("foo")

    assertThat(resolver.revisionOf(record)).isEqualTo("4711")
  }
}
