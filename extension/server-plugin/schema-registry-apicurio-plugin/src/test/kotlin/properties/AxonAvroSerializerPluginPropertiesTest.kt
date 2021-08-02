package io.holixon.axon.avro.registry.plugin.properties

import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties.Companion.DEFAULT_HOST
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties.Companion.DEFAULT_PORT
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties.Companion.EMPTY
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties.Companion.KEY_HOST
import io.holixon.axon.avro.registry.plugin.apicurio.properties.AxonAvroSchemaRegistryApicurioPluginProperties.Companion.KEY_PORT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AxonAvroSerializerPluginPropertiesTest {
  companion object {
    fun createProperties(host: String, port: Int) = mapOf(
      KEY_HOST to host,
      KEY_PORT to port
    )
  }

  @Test
  internal fun `configure by map`() {
    val map = createProperties("localhost", 8080)
    val properties = AxonAvroSchemaRegistryApicurioPluginProperties(map)

    assertThat(properties.host).isEqualTo("localhost")
    assertThat(properties.port).isEqualTo(8080)
  }

  @Test
  internal fun `empty when map is null`() {
    assertThat(AxonAvroSchemaRegistryApicurioPluginProperties(null)).isEqualTo(EMPTY)
  }

  @Test
  internal fun `default values when map is empty`() {
    val properties = AxonAvroSchemaRegistryApicurioPluginProperties(emptyMap())

    assertThat(properties).isEqualTo(
      AxonAvroSchemaRegistryApicurioPluginProperties(
        host = DEFAULT_HOST,
        port = DEFAULT_PORT
      )
    )
  }
}
