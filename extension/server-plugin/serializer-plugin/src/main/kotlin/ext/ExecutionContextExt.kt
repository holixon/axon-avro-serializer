package io.holixon.axon.avro.serializer.plugin.ext

import io.axoniq.axonserver.plugin.ExecutionContext
import io.holixon.axon.avro.serializer.plugin.api.ContextName

object ExecutionContextExt {

  private const val PRINCIPAL_EVENT_HANDLER = "<anonymous>"

  fun ExecutionContext.isDashboardRequest() = PRINCIPAL_EVENT_HANDLER != this.principal()

  private fun <T> Any.getPrivateFieldValue(name: String): T {
    val field = this::class.java.getDeclaredField(name)
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(this) as T
  }

  fun ExecutionContext.details(): Map<String, Any?>? = this.getPrivateFieldValue("details")

  data class ExecutionContextData(
    val context: ContextName?,
    val principal: String?,
    val principalRoles: Set<String?>?,
    val principalTags: Map<String?, String?>?,
    val details: Map<String, Any?>?
  )

  fun ExecutionContext.data() = ExecutionContextData(
    context = this@data.contextName(),
    principal = this@data.principal(),
    principalRoles = this@data.principalRoles(),
    principalTags = this@data.principalTags(),
    details = this@data.details()
  )
}
