package io.holixon.axon.avro.serializer.plugin.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AxonAvroSerializerPluginPropertiesTest {
  companion object {
    fun createProperties(host: String, port: Int) = mapOf(
      AxonAvroSerializerPluginProperties.KEY_HOST to host,
      AxonAvroSerializerPluginProperties.KEY_PORT to port
    )
  }

  @Test
  internal fun `configure by map`() {
    val map = createProperties("localhost", 8080)
    val properties = AxonAvroSerializerPluginProperties(map)

    assertThat(properties.host).isEqualTo("localhost")
    assertThat(properties.port).isEqualTo(8080)
  }

  @Test
  internal fun `empty when map is null`() {
    assertThat(AxonAvroSerializerPluginProperties(null)).isEqualTo(AxonAvroSerializerPluginProperties.EMPTY)
  }

  @Test
  internal fun `default values when map is empty`() {
    val properties = AxonAvroSerializerPluginProperties(emptyMap())

    assertThat(properties).isEqualTo(
      AxonAvroSerializerPluginProperties(
        host = AxonAvroSerializerPluginProperties.DEFAULT_HOST,
        port = AxonAvroSerializerPluginProperties.DEFAULT_PORT
      )
    )
  }
}
