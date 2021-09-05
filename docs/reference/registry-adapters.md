The [Avro Registry Adapter](https://github.com/holixon/avro-registry-adapter) project provides several registry adapter, which need
to be configured for use with the Avro Serializer.

## Apicurio Registry Adapter 

The Apicurio registry adapter requires a connection configuration including the host and the port of the registry.

```kotlin

  @Bean
  fun apicurioRegistryClient(
    @Value("\${apicurio.registry.host}") host: String,
    @Value("\${apicurio.registry.port}") port: Int
  ): RegistryClient = AvroAdapterApicurioRest.registryRestClient(host, port)

  @Bean
  fun avroSchemaRegistry(apicurioRegistryClient: RegistryClient) = ApicurioAvroSchemaRegistry(
    client = GroupAwareRegistryClient(apicurioRegistryClient, AvroAdapterDefault.schemaIdSupplier, AvroAdapterDefault.schemaRevisionResolver),
    schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
    schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
  )
```
