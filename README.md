# axon-avro-serializer

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e5518754d4fd4eea80ef02a95be59486)](https://app.codacy.com/gh/holixon/axon-avro-serializer?utm_source=github.com&utm_medium=referral&utm_content=holixon/axon-avro-serializer&utm_campaign=Badge_Grade_Settings)
[![Build Status](https://github.com/holixon/axon-avro-serializer/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-avro-serializer/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.avro/axon-avro-serializer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.avro/axon-avro-serializer)
[![codecov](https://codecov.io/gh/holixon/axon-avro-serializer/branch/develop/graph/badge.svg?token=ZKDNW1QJ1Y)](https://codecov.io/gh/holixon/axon-avro-serializer)

This extension to the [Axon Framework](https://docs.axoniq.io/reference-guide/) aims to provide support for serialization of Axon events
with the [Apache Avro](https://avro.apache.org/docs/current/) data format.

Avro is a schema-based data format that can be serialized to JSON or byte sequence, which is useful to minimize the disk space needed for
your Axon event store and bandwidth used for transport. Messages can be validated against a schema version and Avro supports schema
evolution by automatically determining [compatibility modes](https://docs.confluent.io/platform/current/schema-registry/avro.html) between
different revisions.

Please check our [official documentation](https://www.holixon.io/axon-avro-serializer/snapshot/) for more details.


## Developers

* Jan Galinski (https://github.com/jangalinski)
* Simon Zambrovski (https://github.com/zambrovski)

## License

This library is developed under

[![Apache 2.0 License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.holunda.io/camunda-bpm-taskpool/license)

## Sponsors and Customers

[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-red.svg)](https://holisticon.de/)

