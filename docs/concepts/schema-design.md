Based on the business requirements for the events of the bounded context, an Avro Schema definition is created. It describes all properties
of the event and is defined using JSON.

Most Important:

* event namespace (in which context is this event relevant? could be context's name prefixed by the domain,
  example: `example.bankaccount.event`)
* event name (describing what happened in a simple past tense. Meaningful in context, example: `BankAccountCreated`)
* event revision (when we later modify the event content, which is the revision we relate to, example: `1`)
* event reference identifier (business aggregate identifier this event relates to, example: `bankAccountId`)

The schema can be identified by the URL of `namespace.name`, which will later also be the FQN of the generated classes, used for handling
the event instances.

!!! important 
        We consider that the namespace of the business context and the name of an event should not change over time when they are
        modelled properly in advance. So it might be a good option not to force a typical java-package pattern on them, which would require renaming 
        when refactorings are done.

The revision should be defined as a metadata of the schema, not a property inside.

Here is an example:

```json
{
  "type": "record",
  "namespace": "example.bankaccount.event",
  "name": "BankAccountCreatedEvent",
  "fields": [
    {
      "name": "bankAccountId",
      "type": "string"
    },
    {
      "name": "initialBalance",
      "type": "int"
    }
  ],
  "revision": "1"
}

```

For more details, please consult the [Avro Specification](https://avro.apache.org/docs/current/spec.html#schemas).


