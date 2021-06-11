@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")
package io.holixon.axon.avro.serializer.spring

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
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait.forLogMessage
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(classes = [AxonAvroSerializerConfigurationITestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("itest")
internal class AxonAvroSerializerConfigurationITest {
  companion object : KLogging() {
    @Container
    val axon = AxonServerContainer()

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) {
      registry.add("axon.axonserver.servers") { axon.url }
    }
  }

  @Autowired
  lateinit var commandGateway: CommandGateway

  @Autowired
  lateinit var queries : CurrentBalanceQueries

  @Autowired
  lateinit var auditEventQuery : BankAccountAuditQuery

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

class AxonServerContainer : GenericContainer<AxonServerContainer>("axoniq/axonserver:4.5.2") {
  companion object : KLogging()

  init {
    withLogConsumer(Slf4jLogConsumer(logger))
    withExposedPorts(8024, 8124)
    waitingFor(forLogMessage(".*Started AxonServer.*\\n", 1))
  }

  val url by lazy {
    val u = "localhost:${getMappedPort(8124)}"
    logger.info { "THE URL IS: $u" }
    u
  }
}

@SpringBootApplication
@Import(AxonAvroSerializerConfiguration::class)
@ComponentScan(basePackageClasses = [BankAccount::class])
class AxonAvroSerializerConfigurationITestApplication {

  @Bean
  fun projection() = CurrentBalanceProjection()

  @Bean
  fun schemaRegistry() = AvroAdapterDefault.inMemorySchemaRegistry().apply {
    register(BankAccountCreated.getClassSchema())
    register(MoneyDeposited.getClassSchema())
    register(MoneyWithdrawn.getClassSchema())
  }

  @Bean
  fun currentBalanceQueries(queryGateway: QueryGateway) : CurrentBalanceQueries = CurrentBalanceQueries(queryGateway)

  @Bean
  fun bankAccountAuditEventQuery(queryGateway: QueryGateway) : BankAccountAuditQuery = BankAccountAuditQuery.create(queryGateway)
}
