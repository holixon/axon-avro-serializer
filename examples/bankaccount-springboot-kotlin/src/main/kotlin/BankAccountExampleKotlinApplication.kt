package io.holixon.axon.avro.examples.bankaccount

import bankaccount.BankAccount
import bankaccount.projection.CurrentBalanceProjection
import org.axonframework.queryhandling.QueryGateway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

fun main(args: Array<String>) = runApplication<BankAccountExampleKotlinApplication>(*args).let { Unit }

@SpringBootApplication
@ComponentScan(basePackageClasses = [BankAccountExampleKotlinApplication::class, BankAccount::class])
class BankAccountExampleKotlinApplication {

  @Bean
  fun projection() = CurrentBalanceProjection()

  @Bean
  fun currentBalanceQueries(queryGateway: QueryGateway) = CurrentBalanceProjection.CurrentBalanceQueries(queryGateway)
}
