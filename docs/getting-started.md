Put the serializer library and one of the [registry adapters](reference/registry-adapters.md) on the class path of you project:

```xml

<dependency>
  <groupId>io.holixon.axon.avro</groupId>
  <artifactId>axon-avro-serializer-spring</artifactId>
  <version>${axon-avro-serializer.version}</version>
</dependency>
```

```xml

<dependency>
  <groupId>io.holixon.avro</groupId>
  <artifactId>avro-registry-adapter-apicurio</artifactId>
  <version>${avro-registry.version}</version>
</dependency>
```

Activate the used registry adapter and the serializer in your configuration:

```kotlin
@EnableAxonAvroSerializer
class MyConfiguration {

  @Bean
  fun avroSchemaRegistry(): AvroSchemaRegistry {
    return ...
  }
}
```
