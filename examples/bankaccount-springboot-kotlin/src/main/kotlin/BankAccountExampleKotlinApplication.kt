package io.holixon.axon.avro.examples.bankaccount

import bankaccount.BankAccount
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.query.CurrentBalanceQueries
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.axon.avro.serializer.spring.EnableAxonAvroSerializer
import org.axonframework.queryhandling.QueryGateway
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

fun main(args: Array<String>) = runApplication<BankAccountExampleKotlinApplication>(*args).let { }

@SpringBootApplication
@ComponentScan(basePackageClasses = [BankAccountExampleKotlinApplication::class, BankAccount::class])
@EnableAxonAvroSerializer
@EnableScheduling
class BankAccountExampleKotlinApplication {

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
  fun xStream() = XStream().apply { this.addPermission(AnyTypePermission.ANY) }

  @Bean
  fun avroSchemaRegistry(apicurioRegistryClient: RegistryClient) = ApicurioAvroSchemaRegistry(
    client = apicurioRegistryClient,
    schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
    schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
  )
}
