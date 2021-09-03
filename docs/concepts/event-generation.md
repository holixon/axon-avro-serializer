In order to work with the Axon Framework, we need to have Java (or Kotlin) classes representing the events defined in the schema to be
available. This class is generated from the schema using Avro generator. In order to do so, the build environment must have access to the
schema (registry) and use a build system plugin (Maven or Gradle) to generate java/kt source files Please see example in
the [reference guide](../reference/index.md#event-generation) for more details.

!!! note The class FQN will suffice to identity the published schema, but for the Axon default upcasters to work, a `@Revision` annotation
on the class file is needed. For this purpose, a special SchemaBasedRevisionResolver is implemented and configured in the Avro serializer.
  

