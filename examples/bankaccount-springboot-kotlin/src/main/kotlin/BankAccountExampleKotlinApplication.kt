package io.holixon.axon.avro.examples.bankaccount

import bankaccount.BankAccount
import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.query.CurrentBalanceQueries
import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.avro.adapter.registry.apicurio.client.GroupAwareRegistryClient
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerConfiguration
import io.holixon.axon.avro.serializer.spring.EnableAxonAvroSerializer
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

fun main(args: Array<String>) = runApplication<BankAccountExampleKotlinApplication>(*args).let { }

@SpringBootApplication
@ComponentScan(basePackageClasses = [BankAccountExampleKotlinApplication::class, BankAccount::class])
@EnableAxonAvroSerializer
class BankAccountExampleKotlinApplication : CommandLineRunner {

  @Bean
  fun projection() = CurrentBalanceProjection()

  @Bean
  fun currentBalanceQueries(queryGateway: QueryGateway) = CurrentBalanceQueries(queryGateway)

  @Bean
  fun apicurioRegistryClient(
    @Value("\${apicurio.registry.host}") host: String,
    @Value("\${apicurio.registry.port}") port: Int
  ): RegistryClient = AvroAdapterApicurioRest.registryRestClient(host, port)

  @Bean
  fun avroSchemaRegistry(apicurioRegistryClient: RegistryClient) = ApicurioAvroSchemaRegistry(
    client = GroupAwareRegistryClient(apicurioRegistryClient, AvroAdapterDefault.schemaIdSupplier, AvroAdapterDefault.schemaRevisionResolver),
    schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
    schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
  )

  @Autowired
  lateinit var schemaRegistry: AvroSchemaRegistry

  override fun run(vararg args: String?) {

    schemaRegistry.register(BankAccountCreated.getClassSchema())
    schemaRegistry.register(MoneyDeposited.getClassSchema())
    schemaRegistry.register(MoneyWithdrawn.getClassSchema())
  }
}
