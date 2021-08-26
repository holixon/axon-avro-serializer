# Architectural Decisions

## 2 - Schema registry

* using the axon-avro-serializer extension must not rely on using a defined vendor/version of an avro-supporting registry.
* For dev/test we will provide a limited registry ourselves (inMem/file based)
* a "good enough" adapter interface should provide plugability for existing registries: Confluent and apicur.io

## 1 - Implementation/Language

* the extension itself will be implemented in kotlin only (latest version, today: 1.4.21). Reason: we need to work with JVM and axon framework and kotlin is more fun.
* The extension should be usable from kotlin and java, although the API might have advantages when using kotlin
* JDK is 11.0 for now 

