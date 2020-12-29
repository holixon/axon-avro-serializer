package converter

import io.holixon.axon.avro.schema.api.AxonAvroSchemaRegistry
import io.holixon.axon.avro.serializer.converter.specificrecord.SpecificRecordToByteArrayConverter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import test.fixture.SampleEvent

@Disabled("fails, check later")
internal class SpecificRecordToByteArrayConverterTest {

  val event = SampleEvent.newBuilder().setValue("aaa").build()

  val schemaRegistry = Mockito.mock(AxonAvroSchemaRegistry::class.java)

  val converter = SpecificRecordToByteArrayConverter(schemaRegistry)

  @Test
  internal fun `convert to byteArray`() {
    val bytes = converter.convert(event)

    println(bytes)
  }
}

