package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.axon.avro.serializer.converter.AxonAvroContentTypeConverters.registerGenericDataRecordConverters
import io.holixon.axon.avro.serializer.converter.AxonAvroContentTypeConverters.registerSpecificRecordConverters
import io.holixon.axon.avro.serializer.ext.SchemaExt.findOrRegister
import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils
import org.axonframework.common.ObjectUtils
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.*
import org.axonframework.serialization.json.JacksonSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Serializer implementation that uses Avro to serialize objects into Single Object format.
 */
class AvroSerializer(
  private val revisionResolver: RevisionResolver,
  private val metaDataSerializer: Serializer,
  private val schemaRegistry: AvroSchemaRegistry,
  private val converter: Converter,
  private val logger: Logger,
  private val myInternalConverter: Converter = ChainingConverter()
) : Serializer {

  companion object {
    @JvmStatic
    fun builder() = Builder()


    /**
     * Instantiate a default [AvroSerializer].
     *
     * The [RevisionResolver] is defaulted to a [SchemaBasedRevisionResolver], the [Converter] to a [ChainingConverter],
     * which is then by initialized by registering the converters for SpecificRecord, GenericData.Record and ByteArray.
     *
     * @return a [AvroSerializer]
     */
    @JvmStatic
    fun defaultSerializer(): AvroSerializer = builder().build()
  }

  constructor(builder: Builder) : this(
    revisionResolver = builder.revisionResolver,
    metaDataSerializer = JacksonSerializer.defaultSerializer(),
    schemaRegistry = builder.schemaRegistry,
    converter = if (builder.converter is ChainingConverter) {
      (builder.converter as ChainingConverter)
        .registerGenericDataRecordConverters(builder.schemaRegistry)
    } else {
      builder.converter
    },
    logger = LoggerFactory.getLogger(AvroSerializer::class.java),
    myInternalConverter = ChainingConverter().apply { // FIXME: don't want the specific converters to be visible to others
      registerSpecificRecordConverters(builder.schemaRegistry)
    }

  )

  override fun classForType(type: SerializedType): Class<*> {
    return if (SimpleSerializedType.emptyType() == type) {
      Void::class.java
    } else {
      ClassUtils.forName(type.name)
    }
  }

  override fun typeForClass(type: Class<*>?): SerializedType = if (type == null || Void.TYPE == type || Void::class.java == type) {
    SimpleSerializedType.emptyType()
  } else if (type is SpecificRecordBase) {
    // FIXME: really?!
    val schema = type.schema.findOrRegister(registry = schemaRegistry)
    SchemaSerializedType(schema)
  } else SimpleSerializedType(type.name, revisionResolver.revisionOf(type))


  override fun getConverter(): Converter = converter

  override fun <T : Any> canSerializeTo(expectedRepresentation: Class<T>): Boolean =
    GenericData.Record::class.java == expectedRepresentation || // FIXME: Ask Steven & Allard
    converter.canConvert(ByteArray::class.java, expectedRepresentation)


//  return JsonNode.class.equals(expectedRepresentation) || String.class.equals(expectedRepresentation) ||
//  converter.canConvert(byte[].class, expectedRepresentation);

  /**
   * @param data: the instance to serialize
   * @param expectedRepresentation: type of target representation
   * @param <T> type of target representation, e.g. ByteArray
   */
  override fun <T : Any> serialize(data: Any, expectedRepresentation: Class<T>): SerializedObject<T> {
    if (data is MetaData) {
      return metaDataSerializer.serialize(data, expectedRepresentation)
    }

    return try {
      require(data is SpecificRecordBase) { "Currently, data must be subtype of SpecificRecordBase, was: ${data.javaClass}" }
//      @Suppress("UNCHECKED_CAST")
//      if (GenericData.Record::class.java == expectedRepresentation) {
//        // FIXME: ask Steven and Allard if this is required.
//        // Why there is a string if branch in Jackson Converter?!
//        val genericDataRecord: GenericData.Record = converter.convert(data, expectedRepresentation)
//        SimpleSerializedObject(
//          genericDataRecord,
//          expectedRepresentation,
//          typeForClass(ObjectUtils.nullSafeTypeOf(data))
//        ) as SimpleSerializedObject<T>
//      } else {
      require(expectedRepresentation == AvroSingleObjectEncoded::class.java) { "Currently, ByteArray is the only allowed representation, was: $expectedRepresentation" }
      val singleObject: T = converter.convert(data, expectedRepresentation)
      logger.debug("Serialized: {} to {}", data, (singleObject as ByteArray).toHexString())
      SimpleSerializedObject(singleObject, expectedRepresentation, typeForClass(ObjectUtils.nullSafeTypeOf(data)))
//      }
    } catch (e: Exception) {
      throw SerializationException("Unable to serialize object into $expectedRepresentation", e)
    }
  }

  override fun <S : Any, T : Any> deserialize(serializedObject: SerializedObject<S>): T? {
    if (SerializedType.emptyType() == serializedObject.type) {
      return null
    }

    val type: Class<*> = classForType(serializedObject.type)
    if (type == MetaData::class.java) {
      return metaDataSerializer.deserialize(serializedObject)
    }

    return try {
      @Suppress("UNCHECKED_CAST")
      when {
        type == UnknownSerializedType::class.java -> {
          UnknownSerializedType(this, serializedObject) as T
        }
        serializedObject.contentType == GenericData.Record::class.java -> {
          (converter.convert(serializedObject, SpecificRecordBase::class.java).data as T)
            .apply {
              logger.debug("deserialized: ${(serializedObject.data as GenericData.Record)} to $this")
            }
        }
        else -> {
          (converter.convert(serializedObject, GenericData.Record::class.java).data as T)
            .apply {
              logger.debug("deserialized: ${(serializedObject.data as ByteArray).toHexString()} to $this")
            }
        }
      }
    } catch (e: Exception) {
      throw SerializationException("Error while deserializing object", e)
    }

  }

  class Builder {
    var revisionResolver: RevisionResolver = SchemaBasedRevisionResolver()
    var converter: Converter = ChainingConverter()
    var schemaRegistry: AvroSchemaRegistry = AvroAdapterDefault.inMemorySchemaRegistry()

    fun revisionResolver(revisionResolver: RevisionResolver) = apply {
      this.revisionResolver = revisionResolver
    }

    fun converter(converter: Converter) = apply {
      this.converter = converter
    }

    fun schemaRegistry(schemaRegistry: AvroSchemaRegistry) = apply {
      this.schemaRegistry = schemaRegistry
    }

    fun build() = AvroSerializer(this)
  }
}
