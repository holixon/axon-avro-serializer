package io.holixon.axon.avro.serializer.converter.specificrecord

import io.holixon.axon.avro.schema.api.AxonAvroSchemaInfo
import io.holixon.axon.avro.schema.api.ext.SchemaExt.info
import org.apache.avro.specific.SpecificRecordBase
import java.nio.ByteBuffer

fun SpecificRecordBase.schemaInfo(): AxonAvroSchemaInfo = schema.info

fun SpecificRecordBase.toByteBuffer() : ByteBuffer = this.javaClass
  .getDeclaredMethod("toByteBuffer")
  .invoke(this) as ByteBuffer


