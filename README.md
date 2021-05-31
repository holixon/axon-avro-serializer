# axon-avro-serializer

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e5518754d4fd4eea80ef02a95be59486)](https://app.codacy.com/gh/holixon/axon-avro-serializer?utm_source=github.com&utm_medium=referral&utm_content=holixon/axon-avro-serializer&utm_campaign=Badge_Grade_Settings)
[![Build Status](https://github.com/holixon/axon-avro-serializer/workflows/Development%20branches/badge.svg)](https://github.com/holixon/axon-avro-serializer/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.avro/axon-avro-serializer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.axon.avro/axon-avro-serializer)


* DISCLAYMER: early draft, heavy development, *everything* might still change

This extension to the [axon framework](https://docs.axoniq.io/reference-guide/) aims to provide support for serialization of axon messages (commands, events, queries) with the [apache avro](https://avro.apache.org/docs/current/) data format.

Avro is a schema based data format that can be serialized to JSON and byte sequence, which is useful to minimize
the disk space needed for your axon event store and bandwidth used for transport.
Messages can be validated against a schema version and avro supports schema evolution by automatically determining
[`compatibility modes`](https://docs.confluent.io/platform/current/schema-registry/avro.html) between different revisions.

see on [component overview](file:/docs/README.md)




## Links

* <https://hub.docker.com/r/axoniq/axonserver>


AVRO Serialization for Axon axon.serializer.[command|event|general]


## Notes

* <https://github.com/sksamuel/avro4k>
* <https://www.baeldung.com/java-apache-avro>
* <https://avro.apache.org/docs/current/gettingstartedjava.html>
* <http://bigdatums.net/2016/01/20/simple-apache-avro-example-using-java/>
* <http://www.soutier.de/blog/2017/03/07/daten-modellieren-avro/>

### great

* <https://blog.cloudera.com/robust-message-serialization-in-apache-kafka-using-apache-avro-part-1/>
* <https://github.com/cloudera/kafka-examples>
