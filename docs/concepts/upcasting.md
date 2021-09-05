Upcasting is the process of matching the structure of the event intermediate representation of an event to the target type of the event. It
is required if the structure of the event of the writer side is not matching the structure of the event of the reader side. In general, we
speak about event evolution, which is described in the context of Avro with the concept of Schema Evolution. Luckily, Avro provides a
foundation of detection of schema changes and allows for a classification of those changes - some of those changes are non-breaking and some
are breaking. The task for the upcaster is to overcome the breaking changes.

In order to upcast a Avro-serialized message, the intermediate representation is constructed. Since it is not easily possible to upcast
binary data directly, we provide a converter of binary data into
a [Generic Record format](https://avro.apache.org/docs/1.7.6/api/java/org/apache/avro/generic/GenericRecord.html) which is essentially
a `Map<String, Object>` structure. An upcaster may modify this map to match the target event type. In addition, it needs to change the
schema information (schema namespace, name, revision) of the `Generic Record` to match the reader schema.
