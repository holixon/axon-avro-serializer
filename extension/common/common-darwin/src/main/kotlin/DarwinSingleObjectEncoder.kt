package io.holixon.axon.avro.common.darwin

import io.holixon.axon.avro.common.AvroSingleObjectEncoded
import io.holixon.axon.avro.common.type.AvroSchemaWithId
import io.holixon.axon.avro.common.SingleObjectEncoder
import java.nio.ByteOrder

class DarwinSingleObjectEncoder : SingleObjectEncoder {
  override fun apply(schemaWithId: AvroSchemaWithId, avroPayload: ByteArray): AvroSingleObjectEncoded {
    return AvroCommonDarwin.connector.generateAvroSingleObjectEncoded(
      avroPayload,
      schemaWithId.schema,
      ByteOrder.LITTLE_ENDIAN
    ) { schemaWithId.id }
  }
}
