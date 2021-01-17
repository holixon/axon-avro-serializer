package io.holixon.axon.avro.common

import bankaccount.BankAccountCreated
import io.holixon.axon.avro.common.AvroCommon.propertyBasedSchemaRevisionResolver
import io.holixon.axon.avro.common.ext.FunctionExt.invoke
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroCommonTest {

  companion object {
    val revisionResolver = propertyBasedSchemaRevisionResolver("revision")
  }


  @Test
  internal fun `return schemaRevision by property`() {
    val schema = BankAccountCreated.getClassSchema()

    val revisionResolver = propertyBasedSchemaRevisionResolver("revision")

    assertThat(revisionResolver(schema)).hasValue("1")
  }
}
