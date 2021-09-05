@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package io.holixon.axon.avro.serializer.spring.itest.bank

import bankaccount.BankAccount
import bankaccount.command.CreateBankAccount
import bankaccount.command.DepositMoney
import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.query.BankAccountAuditQuery
import bankaccount.query.CurrentBalanceQueries
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerConfiguration
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerSpringBase
import io.holixon.axon.avro.serializer.spring.AxonAvroSerializerSpringBase.PROFILE_ITEST
import io.holixon.axon.avro.serializer.spring.container.AxonServerContainer
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(classes = [AxonAvroSerializerConfigurationITestApplication::class], webEnvironment = RANDOM_PORT)
@Testcontainers
@ActiveProfiles(PROFILE_ITEST)
internal class AxonAvroSerializerConfigurationITest {
  companion object : KLogging() {
    @Container
    val axon = AxonServerContainer()

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)
  }

  @Autowired
  lateinit var commandGateway: CommandGateway

  @Autowired
  lateinit var queries: CurrentBalanceQueries

  @Autowired
  lateinit var auditEventQuery: BankAccountAuditQuery

  @Test
  internal fun `create account and deposit money`() {
    val accountId = UUID.randomUUID().toString()

    assertThat(queries.findByAccountId(accountId).join()).isEmpty

    commandGateway.sendAndWait<Any>(CreateBankAccount(accountId, 100))

    await.untilAsserted {
      assertThat(queries.findByAccountId(accountId).join()).isNotEmpty
    }

    val auditEvents = auditEventQuery.apply(accountId)
    assertThat(auditEvents.events).isNotEmpty
    assertThat(auditEvents.events.first().correlationId).isNotNull

    logger.info { "auditEvents for accountId='$accountId': ${auditEventQuery.apply(accountId)}" }

    commandGateway.sendAndWait<Any>(DepositMoney(accountId, 50))

    await.untilAsserted {
      assertThat(queries.findByAccountId(accountId).join().orElseThrow().balance).isEqualTo(150)
    }

    logger.info { "auditEvents for accountId='$accountId': ${auditEventQuery.apply(accountId)}" }
  }
}

@SpringBootApplication
@Import(AxonAvroSerializerConfiguration::class)
@ComponentScan(basePackageClasses = [BankAccount::class])
@Profile(PROFILE_ITEST)
class AxonAvroSerializerConfigurationITestApplication {

  @Bean
  fun projection() = CurrentBalanceProjection()

  @Bean
  fun schemaRegistry() = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(
    BankAccountCreated.getClassSchema(),
    MoneyDeposited.getClassSchema(),
    MoneyWithdrawn.getClassSchema()
  )

  @Bean
  fun currentBalanceQueries(queryGateway: QueryGateway): CurrentBalanceQueries = CurrentBalanceQueries(queryGateway)

  @Bean
  fun bankAccountAuditEventQuery(queryGateway: QueryGateway): BankAccountAuditQuery = BankAccountAuditQuery.create(queryGateway)
}
