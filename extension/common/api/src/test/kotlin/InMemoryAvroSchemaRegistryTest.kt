package io.holixon.axon.avro.common

import bankaccount.event.BankAccountCreated
import io.holixon.axon.avro.common.AvroCommon.propertyBasedSchemaRevisionResolver
import io.holixon.axon.avro.common.ext.SchemaExt.fingerprint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class InMemoryAvroSchemaRegistryTest {

  private val registry = AvroCommon.InMemoryAvroSchemaRegistry(
    schemaIdSupplier = SchemaIdSupplier.DEFAULT,
    schemaRevisionResolver = propertyBasedSchemaRevisionResolver("revision")
  )

  private val schema = SampleEvent.getClassSchema()
  private val id = SchemaIdSupplier.DEFAULT.apply(schema)
  private val sample = SampleEvent("foo")

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll()).isEmpty()
  }

  @Test
  internal fun `register returns schemaWithId`() {
    val registered = registry.register(SampleEvent.getClassSchema())

    assertThat(registry.findAll()).hasSize(1)
    assertThat(registered.id).isEqualTo(SampleEvent.getClassSchema().fingerprint)
    assertThat(registered.schema).isEqualTo(SampleEvent.getClassSchema())
    assertThat(registered.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(registered.revision).isEqualTo("4711")
  }

  @Test
  internal fun `find by context and name`() {
    assertThat(registry.findByContextAndName("bankaccount", "BankAccountCreated")).isEmpty()
    registry.register(BankAccountCreated.getClassSchema())

    assertThat(registry.findByContextAndName("bankaccount.event", "BankAccountCreated")).hasSize(1)
  }

  @Test
  internal fun `can register schema and find by id`() {
    registry.register(SampleEvent.getClassSchema())
    assertThat(registry.findAll()).isNotEmpty

    val found = registry.findById(SchemaIdSupplier.DEFAULT.apply(SampleEvent.getClassSchema())).orElseThrow()

    assertThat(found.id).isEqualTo(id)
    assertThat(found.schema).isEqualTo(schema)
    assertThat(found.revision).isEqualTo("4711")
  }

}
