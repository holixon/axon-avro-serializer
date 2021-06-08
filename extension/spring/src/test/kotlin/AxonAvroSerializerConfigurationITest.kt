package io.holixon.axon.avro.serializer.spring

import bankaccount.BankAccount
import bankaccount.command.CreateBankAccount
import bankaccount.event.BankAccountCreated
import bankaccount.event.MoneyDeposited
import bankaccount.event.MoneyWithdrawn
import bankaccount.projection.CurrentBalanceProjection
import bankaccount.projection.CurrentBalanceProjection.CurrentBalanceQueries
import io.holixon.avro.adapter.common.AvroAdapterDefault
import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.containers.wait.strategy.Wait.forLogMessage
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.temporal.ChronoUnit

@SpringBootTest(classes = [AxonAvroSerializerConfigurationITestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
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
  lateinit var projection : CurrentBalanceProjection

  @Test
  internal fun name() {
    assertThat(queries.findByAccountId("1").join()).isEmpty

    commandGateway.sendAndWait<Any>(CreateBankAccount("1", 100))

    await.untilAsserted {
      assertThat(queries.findByAccountId("1").join()).isNotEmpty
    }

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
  fun currentBalanceQueries(queryGateway: QueryGateway) = CurrentBalanceQueries(queryGateway)
}
