package io.holixon.axon.avro.common.darwin

import io.holixon.axon.avro.common.*
import io.holixon.axon.avro.common.darwin.AvroCommonDarwin.darwin
import io.holixon.axon.avro.common.type.AvroPayloadAndSchema
import java.nio.ByteOrder

class DarwinSingleObjectDecoder(
  private val schemaResolver: SchemaResolver
) : SingleObjectDecoder {
  override fun apply(payload: AvroSingleObjectEncoded): AvroPayloadAndSchema {

    val r = AvroCommonDarwin.connector.retrieveSchemaAndAvroPayload(payload, ByteOrder.LITTLE_ENDIAN, schemaResolver.darwin())

    return AvroCommon.AvroPayloadAndSchemaData(
      payload = r._2,
      schema = AvroCommon.AvroSchemaWithIdData(
        id = AvroCommonDarwin.connector.extractId(payload, ByteOrder.LITTLE_ENDIAN),
        schema = r._1
      )
    )
  }
}
