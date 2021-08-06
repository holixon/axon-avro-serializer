package io.holixon.axon.avro.registry.plugin.apicurio.cache

import bankaccount.event.BankAccountCreated
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class CachingSchemaResolverTest {
  val bankAccountCreated = BankAccountCreated.getClassSchema().avroSchemaWithId

  val cachingResolver = CachingSchemaResolver(InMemorySchemaResolver(bankAccountCreated))

  @Test
  internal fun `use cache`() {
    assertThat(cachingResolver.apply(bankAccountCreated.schemaId))
      .hasValue(bankAccountCreated)
  }

  class InMemorySchemaResolver(vararg schemas: AvroSchemaWithId) : AvroSchemaResolver {
    val map = schemas.associateBy { it.schemaId }

    override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(map[schemaId])
  }
}
