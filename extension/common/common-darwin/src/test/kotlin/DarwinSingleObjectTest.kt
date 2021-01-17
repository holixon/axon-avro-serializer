package io.holixon.axon.avro.common.darwin


import io.holixon.axon.avro.common.SchemaIdSupplier
import io.holixon.axon.avro.common.ext.ByteArrayExt.toHexString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal class DarwinSingleObjectTest {


  @Test
  internal fun name() {
    val sample = SampleEvent("foo")
    println(sample.toString())

    val bytes = sample.toByteBuffer().array()

    println(SchemaIdSupplier.DEFAULT.apply(sample.schema))
    println(AvroCommonDarwin.connector.extractId(bytes, ByteOrder.LITTLE_ENDIAN))

    println(bytes.toHexString())

    println(SampleEvent.fromByteBuffer(ByteBuffer.wrap(bytes)))

  }
}
