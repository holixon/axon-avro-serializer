package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
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
    TODO("Not yet implemented")
  }
}
