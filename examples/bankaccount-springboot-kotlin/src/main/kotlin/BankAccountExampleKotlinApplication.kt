package io.holixon.axon.avro.examples.bankaccount

import bankaccount.BankAccount
import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import bankaccount.projection.CurrentBalanceProjection
import io.holixon.axon.avro.common.AvroSchemaRegistry
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerConfiguration
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

fun main(args: Array<String>) = runApplication<BankAccountExampleKotlinApplication>(*args).let { Unit }

@SpringBootApplication
@ComponentScan(basePackageClasses = [BankAccountExampleKotlinApplication::class, BankAccount::class])
@Import(AxonAvroSerializerConfiguration::class)
class BankAccountExampleKotlinApplication : CommandLineRunner{

  @Bean
  fun projection() = CurrentBalanceProjection()

  @Bean
  fun currentBalanceQueries(queryGateway: QueryGateway) = CurrentBalanceProjection.CurrentBalanceQueries(queryGateway)

  @Autowired
  lateinit var schemaRegistry: AvroSchemaRegistry

  override fun run(vararg args: String?) {

    schemaRegistry.register(BankAccountCreated.getClassSchema())
    schemaRegistry.register(MoneyDeposited.getClassSchema())
    schemaRegistry.register(MoneyWithdrawn.getClassSchema())
  }
}
