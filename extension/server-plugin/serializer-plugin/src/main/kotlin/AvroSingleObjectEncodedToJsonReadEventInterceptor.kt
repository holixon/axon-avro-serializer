package io.holixon.axon.avro.serializer.plugin

import com.google.protobuf.ByteString
import io.axoniq.axonserver.grpc.SerializedObject
import io.axoniq.axonserver.grpc.event.Event
import io.axoniq.axonserver.plugin.ExecutionContext
import io.axoniq.axonserver.plugin.interceptor.ReadEventInterceptor
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault.isAvroSingleObjectEncoded
import io.holixon.axon.avro.registry.plugin.SingleObjectToJsonConverterProvider
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.data
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.isDashboardRequest
import mu.KLogging

/**
 * If the event payload is single object encoded, replace the payload bytes with the json representation of the event.
 */
class AvroSingleObjectEncodedToJsonReadEventInterceptor(
  private val singleObjectToJsonConverterProvider: SingleObjectToJsonConverterProvider
) : ReadEventInterceptor {
  companion object : KLogging()

  override fun readEvent(event: Event, executionContext: ExecutionContext): Event {

    if (!executionContext.isDashboardRequest()) {
      logger.debug { "Request is not coming from dashboard, just passing event: ${event.messageIdentifier}" }
      return event
    }

    val payloadBytes = event.payload.data.toByteArray()

    if (!payloadBytes.isAvroSingleObjectEncoded()) {
      logger.warn { "Not converting, because not single object encoded: ${payloadBytes.toHexString()}" }
      return event
    }

    logger.info { "Event: $event" }
    logger.info { "ExecutionContextData: ${executionContext.data()}" }

    return try {
      logger.info { "Using JSON converter $singleObjectToJsonConverterProvider" }
      val json = singleObjectToJsonConverterProvider.get(executionContext.contextName()).convert(payloadBytes)
      logger.info { "JSON from registry: $json" }
      Event.newBuilder(event)
        .setPayload(
          SerializedObject.newBuilder(event.payload)
            .setData(ByteString.copyFrom(json, Charsets.UTF_8))
            .build()
        )
        .build()
    } catch (e: Exception) {
      logger.error { "Could not convert: ${e.message}\n${e.stackTraceToString()}" }
      event
    }
  }
}
