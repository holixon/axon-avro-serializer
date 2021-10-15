package io.holixon.axon.avro.serializer.spring

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.axon.avro.serializer.AvroSerializer
import org.axonframework.serialization.Serializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * Configuration for Axon serializer.
 */
open class AxonAvroSerializerConfiguration {
  companion object {
    const val EVENT_SERIALIZER = "eventSerializer"
  }

  /**
   * Bean factory for the serializer builder.
   */
  @Bean
  @ConditionalOnMissingBean(AvroSerializer.Builder::class)
  fun defaultAxonSerializerBuilder(schemaRegistry: AvroSchemaReadOnlyRegistry): AvroSerializer.Builder = AvroSerializer.builder()
    .schemaRegistry(schemaRegistry)

  /**
   * Bean factory for the serializer.
   */
  @Bean
  @Qualifier(EVENT_SERIALIZER)
  fun avroSerializer(builder: AvroSerializer.Builder): Serializer = AvroSerializer(builder)

}
