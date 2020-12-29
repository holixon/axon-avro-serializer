package io.holixon.axon.avro.schema.adapter.apicurio

import io.apicurio.registry.client.RegistryRestClient
import io.holixon.axon.avro.schema.api.AxonAvroSchema
import io.holixon.axon.avro.schema.api.AxonAvroSchemaApi
import io.holixon.axon.avro.schema.api.AxonAvroSchemaInfo
import io.holixon.axon.avro.schema.api.AxonAvroSchemaRegistry
import org.apache.avro.Schema
import java.io.BufferedReader
import java.util.*

class ApicurioAxonAvroSchemaRegistry(
  val client: RegistryRestClient,
  val cache: AxonAvroSchemaApi.InMemoryAxonAvroSchemaRegistry
) : AxonAvroSchemaRegistry {

  override fun register(schema: Schema): AxonAvroSchema {
    TODO("Not yet implemented")
  }

  override fun findById(globalId: Long): Optional<AxonAvroSchema> = cache.findById(globalId).or {
    Optional.of(cache.register(
      Schema.Parser()
        .parse(client.getArtifactByGlobalId(globalId)))
    )
  }

  override fun findByInfo(info: AxonAvroSchemaInfo): Optional<AxonAvroSchema> {
    TODO("Not yet implemented")
  }

  override fun findByContextAndName(context: String, name: String): List<AxonAvroSchema> {
    TODO("Not yet implemented")
  }
}
