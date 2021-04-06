package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.serializer.converter.AxonAvroContentTypeConverters.registerSpecificRecordConverters
import io.holixon.axon.avro.serializer.ext.SchemaExt.findOrRegister
import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
import io.toolisticon.avro.adapter.api.AvroAdapterApi.toHexString
import io.toolisticon.avro.adapter.api.AvroSchemaRegistry
import io.toolisticon.avro.adapter.common.AvroAdapterDefault
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
  private val logger: Logger
) : Serializer {

  companion object {
    @JvmStatic
    fun builder() = Builder()


    /**
     * Instantiate a default [AvroSerializer].
     *
     * The [RevisionResolver] is defaulted to a [SchemaBasedRevisionResolver], the [Converter] to a [ChainingConverter],
     * which is then by initialized by registering the converters for Specificrecord and ByteArray.
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
      (builder.converter as ChainingConverter).registerSpecificRecordConverters(builder.schemaRegistry)
    } else {
      builder.converter
    },
    logger = LoggerFactory.getLogger(AvroSerializer::class.java)
  )

  override fun classForType(type: SerializedType): Class<*> {
    return if (SimpleSerializedType.emptyType() == type) {
      Void::class.java
    } else  {
      ClassUtils.forName(type.name)
    }
  }

  override fun typeForClass(type: Class<*>?): SerializedType = if (type == null || Void.TYPE == type || Void::class.java == type) {
    SimpleSerializedType.emptyType()
  } else if (type is SpecificRecordBase) {
    val schema = type.schema.findOrRegister(registry = schemaRegistry)
    SchemaSerializedType(schema)
  } else SimpleSerializedType(type.name, revisionResolver.revisionOf(type))


  override fun getConverter(): Converter = converter

  override fun <T : Any> canSerializeTo(expectedRepresentation: Class<T>): Boolean = converter.canConvert(ByteArray::class.java, expectedRepresentation)

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
    require(expectedRepresentation == ByteArray::class.java) { "Currently, ByteArray is the only allowed representation, was: $expectedRepresentation" }
    require(data is SpecificRecordBase) { "Currently, data must be subtype of SpecificRecordBase, was: ${data.javaClass}" }

    val singleObject: T = converter.convert(data, expectedRepresentation)
    logger.debug("serialized: {} to {}", data, (singleObject as ByteArray).toHexString())

    return SimpleSerializedObject(singleObject, expectedRepresentation, typeForClass(ObjectUtils.nullSafeTypeOf(data)))
  }

  override fun <S : Any, T : Any> deserialize(serializedObject: SerializedObject<S>): T? {
    if (SerializedType.emptyType() == serializedObject.type) return null
    val type: Class<*> = classForType(serializedObject.type)

    if (type == UnknownSerializedType::class.java) {
      return UnknownSerializedType(this, serializedObject) as T
    }
    if (type == MetaData::class.java) {
      return metaDataSerializer.deserialize(serializedObject)
    }

    return (converter.convert(serializedObject, SpecificRecordBase::class.java).data as T)
      .apply {
        logger.debug("deserialized: ${(serializedObject.data as ByteArray).toHexString()} to $this")
      }

  }

  class Builder {
    var revisionResolver: RevisionResolver = SchemaBasedRevisionResolver()
    var converter: Converter = ChainingConverter()
    var schemaRegistry: AvroSchemaRegistry = AvroAdapterDefault.inMemorySchemaRepository()

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
