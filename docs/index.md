## Why should I use it?

We believe that serialization of events for messaging and storage purpose is done best
using [Apache Avro](https://avro.apache.org/docs/current/) data serialization system. For this purpose we designed and implemented an **Avro
Serializer** for [Axon Framework](https://docs.axoniq.io/reference-guide/) **to foster** usage of Apache Avro as
a [serialization format for events](https://docs.axoniq.io/reference-guide/axon-framework/events/event-serialization), instead of XML or
JSON.

### Why?

* Avro is schema based
* Avro Schema allows detection of incompatibilities during Schema Evolution
* Avro supports binary encoding which saves space

## Interested?

You can get more details if you start understanding [Concepts](concepts/index.md). For further details, please check
our [reference guide](reference/index.md). We also provide
some [Examples](https://github.com/holixon/axon-avro-serializer/tree/develop/examples) demonstrating the usage of the extension in a simple
Axon Bank Scenario.

[![Slack](https://img.shields.io/badge/slack-@holixon/avroserializer-green.svg?logo=slack")](https://holixon.slack.com/messages/avro-serializer/)
[![Github Issues](https://img.shields.io/github/issues/holixon/axon-avro-serializer)](https://github.com/holixon/axon-avro-serializer/issues)



