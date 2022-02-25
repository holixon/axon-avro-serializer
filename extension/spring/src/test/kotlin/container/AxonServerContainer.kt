package io.holixon.axon.avro.serializer.spring.container

import mu.KLogging
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * Axon test container.
 */
class AxonServerContainer() : GenericContainer<AxonServerContainer>("axoniq/axonserver:4.5.8") {
  companion object : KLogging()

  init {
    withLogConsumer(Slf4jLogConsumer(logger))
    withExposedPorts(8024, 8124)
    withEnv("AXONIQ_AXONSERVER_DEVMODE_ENABLED", "true")
    waitingFor(Wait.forLogMessage(".*Started AxonServer.*\\n", 1))
  }

  val restUrl by lazy {
    "http://localhost:${getMappedPort(8024)}"
  }

  val url by lazy {
    "localhost:${getMappedPort(8124)}"
  }

  fun addDynamicProperties(registry: DynamicPropertyRegistry) {
    registry.add("axon.axonserver.servers") { url }
  }
}
