package io.holixon.axon.avro.serializer._itest

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [AvroSerializerITestApplication::class])
@AutoConfigureMockMvc
@Testcontainers
internal class AvroSerializerITest {
  companion object {
    @Container
    val axon = AxonServerContainer

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) {
      registry.add("axon.axonserver.servers") { axon.servers }
    }
  }

  @Test
  internal fun name() {

  }
}

fun main(args: Array<String>) = runApplication<AvroSerializerITestApplication>(*args).let {  }

@SpringBootApplication
internal class AvroSerializerITestApplication


// defined as a singleton in kotlin
object AxonServerContainer : GenericContainer<AxonServerContainer>("axoniq/axonserver") {
  init {
    withExposedPorts(8024, 8124)
    // the container is ready when the below log message is posted
    waitingFor(Wait.forLogMessage(".*Started AxonServer.*\\n", 1))
  }

  // this will allow us to know which host/port to connect to
  val servers: String get() = "localhost:${getMappedPort(8124)}"
}
