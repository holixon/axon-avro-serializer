package io.holixon.axon.avro.common.type

interface AvroPayloadAndSchema {

  val schema: AvroSchemaWithId

  val payload: ByteArray

}
