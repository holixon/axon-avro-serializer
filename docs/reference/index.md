## Encoding

Apache Avro provides several encoding formats. The current extension
uses [Single Object Encoded format](https://avro.apache.org/docs/current/spec.html#single_object_encoding) for binary data representation.
Using this format, the encoded data is **binary encoded** and contains a special **CRC-64-AVRO fingerprint** of the object's schema. The
extension is using this fingerprint to deduce the schema id and resolve the schema from the
the [Schema Registry](../concepts/schema-registry.md) using the configured adapter.

## Event generation

In order to generate the events from Avro schema files using Maven, please use the following plugin:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro-maven-plugin</artifactId>
        <executions>
          <execution>
            <phase>generate-sources</phase>
            <goals>
              <goal>schema</goal>
            </goals>
            <configuration>
              <sourceDirectory>${project.basedir}/src/main/resources/avro/</sourceDirectory>
              <outputDirectory>${project.build.directory}/generated-sources/avro/</outputDirectory>
              <stringType>String</stringType>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```



## Serialization

To use Avro serialization in your application, Axon Serializer needs to be registered for the Eventbus. This requires implementing
the `org.axonframework.serialization.Serializer` interface declared in `axon-messaging`.

!!! note Axon Spring Autoconfiguration allows setting the serializers via `application.yml`. Currently, it only allows to select
between `jackson` and `xstream` and is not used.

The serializer receives the event instance of the generated class. It then:

* validates, if the instance fulfills the requirements (structure, required fields) defined in the schema. For this it needs to:
  * determine schema URL and revision based on class meta data
  * load the schema from the registry (and probably cache it)
* Creates a `serializedType` and `serializedObject` passed over to the eventbus / eventstore

## Deserializing

Since Axon Framework dispatches events to `EventHandlers` based on reflection and FQN, a system consuming events must also keep a java/kt
class based on (= generated from) the schema. Since Avro takes care of checking compatibility between publisher and receiver schema, this
does not have to be the same schema used to serialize the event. See [Upcasting Section](../concepts/upcasting.md) for more details.

