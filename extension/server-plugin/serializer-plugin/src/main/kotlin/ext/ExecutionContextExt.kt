package io.holixon.axon.avro.serializer.plugin.ext

import io.axoniq.axonserver.plugin.ExecutionContext
import io.holixon.axon.avro.serializer.plugin.api.ContextName

/**
 * Helper to work with execution context.
 */
object ExecutionContextExt {

  /*
   * Anonymous principal handler.
   */
  private const val PRINCIPAL_EVENT_HANDLER = "<anonymous>"

  /**
   * Checks if the current request from the dashboard.
   */
  fun ExecutionContext.isDashboardRequest() = PRINCIPAL_EVENT_HANDLER != this.principal()

  private fun <T> Any.getPrivateFieldValue(name: String): T {
    val field = this::class.java.getDeclaredField(name)
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(this) as T
  }

  /**
   * Read details from the context.
   */
  fun ExecutionContext.details(): Map<String, Any?>? = this.getPrivateFieldValue("details")

  /**
   * Context information stored in the execution contenxt.
   */
  data class ExecutionContextData(
    val context: ContextName?,
    val principal: String?,
    val principalRoles: Set<String?>?,
    val principalTags: Map<String?, String?>?,
    val details: Map<String, Any?>?
  )

  /**
   * Reads context data out of the execution context.
   */
  fun ExecutionContext.data() = ExecutionContextData(
    context = this@data.contextName(),
    principal = this@data.principal(),
    principalRoles = this@data.principalRoles(),
    principalTags = this@data.principalTags(),
    details = this@data.details()
  )
}
