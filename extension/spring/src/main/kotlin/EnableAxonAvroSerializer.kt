package io.holixon.axon.avro.serializer.spring

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import org.springframework.context.annotation.Import

/**
 * Annotation to switch the Axon Avro Serialization for events.
 * Will require [AvroSchemaReadOnlyRegistry] bean to be available, usually provided by one of the Registry Adapters for more details.
 */
@MustBeDocumented
@Import(AxonAvroSerializerConfiguration::class)
annotation class EnableAxonAvroSerializer
