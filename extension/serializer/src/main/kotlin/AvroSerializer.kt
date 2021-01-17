package io.holixon.axon.avro.serializer

import io.holixon.axon.avro.common.AvroSchemaRegistry
import io.holixon.axon.avro.serializer.AxonAvroExtension.defaultInMemorySchemaRegistry
import io.holixon.axon.avro.serializer.converter.AxonAvroContentTypeConverters.registerSpecificRecordConverters
import io.holixon.axon.avro.serializer.ext.SchemaExt.findOrRegister
import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils
import org.axonframework.common.ObjectUtils
import org.axonframework.serialization.*

/**
 * Serializer implementation that uses Avro to serialize objects into Single Object format.
 */
class AvroSerializer(
  private val revisionResolver: RevisionResolver,
  private val schemaRegistry: AvroSchemaRegistry,
  private val converter: Converter
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
    schemaRegistry = builder.schemaRegistry,
    converter = if (builder.converter is ChainingConverter) {
      (builder.converter as ChainingConverter).registerSpecificRecordConverters(builder.schemaRegistry)
    } else {
      builder.converter
    }
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
    require(expectedRepresentation == ByteArray::class.java) { "Currently, ByteArray is the only allowed representation" }
    require(data is SpecificRecordBase) { "Currently, data must be subtype of SpecificRecordBase" }

    val singleObject: T = converter.convert(data, expectedRepresentation)

    return SimpleSerializedObject(singleObject, expectedRepresentation, typeForClass(ObjectUtils.nullSafeTypeOf(data)))
  }

  //  //
//  @Override
//  public <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation) {
//    try {
//      if (String.class.equals(expectedRepresentation)) {
//        //noinspection unchecked
//        return new SimpleSerializedObject<>((T) getWriter().writeValueAsString(object), expectedRepresentation,
//        typeForClass(ObjectUtils.nullSafeTypeOf(object)));
//      }
//
//      byte[] serializedBytes = getWriter().writeValueAsBytes(object);
//      T serializedContent = converter.convert(serializedBytes, expectedRepresentation);
//      return new SimpleSerializedObject<>(serializedContent, expectedRepresentation,
//      typeForClass(ObjectUtils.nullSafeTypeOf(object)));
//    } catch (JsonProcessingException e) {
//      throw new SerializationException("Unable to serialize object", e);
//    }
//  }
  override fun <S : Any, T : Any> deserialize(serializedObject: SerializedObject<S>): T? {
    if (SerializedType.emptyType() == serializedObject.type) return null
    val type = classForType(serializedObject.type)
    if (type is UnknownSerializedType) {
      return UnknownSerializedType(this, serializedObject) as T
    }
    return converter.convert(serializedObject, SpecificRecordBase::class.java).data as T

  }

//
//  try {
//    if (JsonNode.class.equals(serializedObject.getContentType())) {
//      return getReader(type)
//        .readValue((JsonNode) serializedObject.getData());
//    }
//    SerializedObject<byte[]> byteSerialized = converter.convert(serializedObject, byte[].class);
//    return getReader(type).readValue(byteSerialized.getData());
//  } catch (IOException e) {
//    throw new SerializationException("Error while deserializing object", e);
//  }



  class Builder {
    var revisionResolver: RevisionResolver = SchemaBasedRevisionResolver()
    var converter: Converter = ChainingConverter()
    var schemaRegistry: AvroSchemaRegistry = defaultInMemorySchemaRegistry()

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
