package io.holixon.axon.avro.registry

import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecord
import org.axonframework.serialization.SerializedType
import org.axonframework.serialization.SimpleSerializedType

/**
 * Data class representing an entry in a schema registry.
 */
data class SchemaSerializedType(
  val id: Int,
  val serializedType: SimpleSerializedType,
  val schema: Schema
) : SerializedType by serializedType {

  companion object {
    // secondary constructor
    operator fun invoke(id: Int, name: String, revision: String, schema: Schema) =
      SchemaSerializedType(id, SimpleSerializedType(name, revision), schema)

    @JvmStatic
    fun <T: SpecificRecord> create(id: Int, recordClass: Class<T>, revision: String) = SchemaSerializedType(
      id = id,
      serializedType = SimpleSerializedType(recordClass.name, revision),
      schema = SpecificData(recordClass.classLoader).getSchema(recordClass)
    )
  }

}
