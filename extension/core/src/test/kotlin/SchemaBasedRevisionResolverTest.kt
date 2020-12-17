package io.holixon.axon.avro

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

}
