## Design decisions

We decided to create a sister project [Avro Registry Adapter](https://github.com/holixon/avro-registry-adapter) which eases the work with
Apache Avro and defines the API and implementations for [Schema Registry](concepts/schema-registry.md). In doing so, we provide a maximum
independence of the serialization process from the used registry.

The extension itself is implemented in Kotlin only, since we need to work with JVM and Axon Framework and Kotlin is more fun.

The extension is usable from Kotlin and Java, although the API might have advantages when using Kotlin.

## References

There are some resources on the Internet, which might be helpful to understand the design decisions and implementation. Here is a short
collection of links:

### Notes

* <https://github.com/sksamuel/avro4k>
* <https://www.baeldung.com/java-apache-avro>
* <https://avro.apache.org/docs/current/gettingstartedjava.html>
* <http://bigdatums.net/2016/01/20/simple-apache-avro-example-using-java/>
* <http://www.soutier.de/blog/2017/03/07/daten-modellieren-avro/>

### Great to read

* <https://blog.cloudera.com/robust-message-serialization-in-apache-kafka-using-apache-avro-part-1/>
* <https://github.com/cloudera/kafka-examples>
