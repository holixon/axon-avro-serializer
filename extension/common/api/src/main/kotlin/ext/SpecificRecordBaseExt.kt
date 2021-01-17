package io.holixon.axon.avro.common.ext

import org.apache.avro.specific.SpecificRecordBase
import java.nio.ByteBuffer

object SpecificRecordBaseExt {

  fun SpecificRecordBase.toByteBuffer() = this.javaClass.getDeclaredMethod("toByteBuffer").invoke(this) as ByteBuffer
  fun SpecificRecordBase.toByteArray() = this.toByteBuffer().array()

  fun Class<SpecificRecordBase>.fromByteArray(bytes: ByteArray) = this.getDeclaredMethod("fromByteBuffer", ByteBuffer::class.java)
    .invoke(null, ByteBuffer.wrap(bytes)) as SpecificRecordBase
}
