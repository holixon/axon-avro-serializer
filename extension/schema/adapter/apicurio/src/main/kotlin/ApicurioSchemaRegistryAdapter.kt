package io.holixon.axon.avro.schema.adapter.apicurio

import io.apicurio.registry.client.RegistryRestClientFactory

object ApicurioSchemaRegistryAdapter {


  fun createClient(baseUrl: String) = RegistryRestClientFactory.create(baseUrl)

}
