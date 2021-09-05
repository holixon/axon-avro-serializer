Axon Server provides a dashboard for operation where you can search for events stored in the system. Since we use binary encoding for
message serialization, the content is not human-readable. Luckily, Axon Server provides a plugin system based on OSGi, allowing to extend
the functionality of the server.

The extension provides a series of Axon Server plugins, to make the search of events inside the Axon Server dashboard usable with Avro
serialized events.

## Axon Server Avro Serializer Plugin

The Axon Server Avro Serializer Plugin is providing the functionality of displaying the event payload in the Axon Server's dashboard. In
doing so, it relies on one of the Axon Server Avro Registry Plugins to access the Schema Registry, required for deserialization.

## Axon Server Avro Registry Plugins

Since the deserialization itself is not coupled to a particular registry, we provide a series of Axon Server Plugins to connect with
the [Schema Registry](../concepts/schema-registry.md) used in your scenario. Currently the following plugins are available:

* Axon Server Apicurio Registry Plugin
* more to come...

## Installation of plugins

1. Download the Axon Server Avro Serializer Plugin and the corresponding Axon Server Avro Registry Plugin binaries
2. Open Axon Server Dashboard and navigate to plugins
3. Install both plugins
4. Configure the Axon Server Avro Registry Plugin to connect to the registry
5. Start both plugins






