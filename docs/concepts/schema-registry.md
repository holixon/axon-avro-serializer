**Schema registry** is a central concept introduced to decouple storage and management of schemas from the usage of schemas for
serialization and deserialization. In order to decouple from concrete implementation, we defined an API to work with registries in the
sister project [Avro Registry Adapter](https://github.com/holixon/avro-registry-adapter). There are several registry adapters available,
which can be used in your implementation. Currently, they are:

* In-Memory registry for a transient registry
* Apicurio Registry Adapter to connect to [apicur.io Registry](https://www.apicur.io/registry/)

The following registries will be available shortly:

* JPA registry for storing schemas in RDBMS
* Confluent registry adapter to work with [Confluent Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html)

Using one of those adapters you can reuse the existing registry. Every registry adapter needs its specific configuration to operate
properly. In the [Registry Adapters](../reference/registry-adapters.md) section, the examples of configuration is provided. For more
details, please consult the documentation of the [Avro Registry Adapter](https://github.com/holixon/avro-registry-adapter) project.

## Registry Performance

Since the Schema registry is required for any serialization and deserialization process, performance of the schema resolution is vital. To
address this requirement, we provide a mechanism of a **Composite Registry**, effectively building a chain of registries. It might be a good
idea to have an in-memory registry as the main registry used in the application, backed by a remote schema registry connected via selected
registry adapter. 

