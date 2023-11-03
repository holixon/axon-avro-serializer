package io.holixon.axon.avro.serializer.plugin

import com.google.protobuf.ByteString
import io.axoniq.axonserver.grpc.SerializedObject
import io.axoniq.axonserver.grpc.event.Event
import io.axoniq.axonserver.plugin.ExecutionContext
import io.axoniq.axonserver.plugin.interceptor.ReadEventInterceptor
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault.isAvroSingleObjectEncoded
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.data
import io.holixon.axon.avro.serializer.plugin.ext.ExecutionContextExt.isDashboardRequest
import io.holixon.axon.avro.serializer.plugin.ext.findSchemaRegistryProvider
import io.holixon.axon.avro.serializer.plugin.ext.usingSingleObjectJsonConverterInContext
import mu.KLogging
import org.osgi.framework.FrameworkUtil


/**
 * If the event payload is single object encoded, replace the payload bytes with the json representation of the event.
 */
class AvroSingleObjectEncodedToJsonReadEventInterceptor : ReadEventInterceptor {
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

    logger.trace { "Event: $event" }
    logger.trace { "ExecutionContextData: ${executionContext.data()}" }

    return try {

      val bundleContext = FrameworkUtil.getBundle(this.javaClass).bundleContext

      bundleContext.usingSingleObjectJsonConverterInContext(
        contextName = executionContext.contextName(),
        serviceReference = bundleContext.findSchemaRegistryProvider()
      ) { jsonConverter ->

        val jsonConverted: JsonStringAndSchemaId = jsonConverter.convert(payloadBytes)
        logger.debug { "JSON from registry: $jsonConverted" }
        val result = Event.newBuilder(event)
          .setPayload(
            SerializedObject.newBuilder(event.payload)
              .setData(ByteString.copyFrom(jsonConverted.json, Charsets.UTF_8))
              .build()
          )
          .build()
        logger.debug { "Resulting event is $result" }
        return result

      }
    } catch (e: Exception) {
      logger.error { "Could not convert: ${e.message}\n${e.stackTraceToString()}" }
      event
    }
  }
}
