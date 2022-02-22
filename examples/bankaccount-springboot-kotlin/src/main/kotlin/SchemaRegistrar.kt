package io.holixon.axon.avro.examples.bankaccount

import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SchemaRegistrar(
  private val schemaRegistry: AvroSchemaRegistry
) {
  @Scheduled(initialDelayString = "PT5S", fixedDelayString = "PT1H")
  fun run() {

    schemaRegistry.register(BankAccountCreated.getClassSchema())
    schemaRegistry.register(MoneyDeposited.getClassSchema())
    schemaRegistry.register(MoneyWithdrawn.getClassSchema())
  }
}
