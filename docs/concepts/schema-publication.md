Once we are happy with our [schema design](schema-design.md), we publish it to a schema registry.

* This extension is not enforcing usage of a particular registry but instead provide
  flexible [registry adapters](../reference/registry-adapters.md).
* The registry must be available at build and runtime because we need it for code generation and runtime validation/migration.
