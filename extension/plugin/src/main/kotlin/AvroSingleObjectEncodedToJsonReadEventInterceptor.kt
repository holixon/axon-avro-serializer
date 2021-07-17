package io.holixon.axon.avro.serializer.plugin

import com.google.protobuf.ByteString
import io.axoniq.axonserver.grpc.SerializedObject
import io.axoniq.axonserver.grpc.event.Event
import io.axoniq.axonserver.plugin.ExecutionContext
import io.axoniq.axonserver.plugin.interceptor.ReadEventInterceptor
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault.isAvroSingleObjectEncoded
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.data
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.isDashboardRequest
import io.holixon.axon.avro.serializer.plugin.properties.AxonAvroSerializerPluginPropertiesProvider
import mu.KLogging

/**
 * If the event payload is single object encoded, replace the payload bytes with the json representation of the event.
 */
class AvroSingleObjectEncodedToJsonReadEventInterceptor(
  private val propertiesProvider: AxonAvroSerializerPluginPropertiesProvider
) : ReadEventInterceptor {
  companion object : KLogging()

  override fun readEvent(event: Event, executionContext: ExecutionContext): Event {
    if (!executionContext.isDashboardRequest()) {
      logger.info { "request is not coming from dashboard, just passing event: ${event.messageIdentifier}" }
      return event
    }
    val payloadBytes = event.payload.data.toByteArray()

    if (!payloadBytes.isAvroSingleObjectEncoded()) {
      logger.warn { "not converting, because not single object encoded: ${payloadBytes.toHexString()}" }
      return event
    }

    logger.info { "event: $event" }
    logger.info { "executionContextData: ${executionContext.data()}" }

    try {
      val json = propertiesProvider.get(executionContext.contextName()).jsonConverter.convert(payloadBytes)

      return Event.newBuilder(event)
        .setPayload(
          SerializedObject.newBuilder(event.payload)
            .setData(ByteString.copyFrom(json, Charsets.UTF_8))
            .build()
        )
        .build()
    } catch (e: Exception) {
      logger.error { "could not convert: ${e.message}\n${e.stackTraceToString()}" }
    }

    return event
  }
}
