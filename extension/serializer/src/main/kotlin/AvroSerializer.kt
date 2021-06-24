package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.converter.SpecificRecordToGenericDataRecordConverter
import io.holixon.avro.adapter.api.converter.SpecificRecordToSingleObjectConverter
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToGenericDataRecordChangingSchemaConverter
import io.holixon.avro.adapter.common.converter.DefaultSpecificRecordToSingleObjectSchemaChangingConverter
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import io.holixon.axon.avro.serializer.converter.AvroSingleObjectEncodedToGenericDataRecordTypeConverter
import io.holixon.axon.avro.serializer.converter.GenericDataRecordToAvroSingleObjectEncodedConverter
import io.holixon.axon.avro.serializer.ext.SchemaExt.find
import io.holixon.axon.avro.serializer.ext.SchemaExt.revision
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
 * Serializer implementation that uses Avro Single Object format for serialization.
 */
class AvroSerializer(
  private val revisionResolver: RevisionResolver,
  private val metaDataSerializer: Serializer,
  private val schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry,
  private val converter: Converter,
  private val logger: Logger,
  private val specificRecordToSingleObjectConverter: SpecificRecordToSingleObjectConverter,
  private val specificRecordToGenericDataRecordConverter: SpecificRecordToGenericDataRecordConverter
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
    schemaReadOnlyRegistry = builder.schemaReadOnlyRegistry,
    converter = if (builder.converter is ChainingConverter) {
      // use GenericData.Record as intermediate representation.
      // register ByteArray to GenericData.Record converter
      // register GenericData.Record to ByteArray converter
      (builder.converter as ChainingConverter).apply {
        this.registerConverter(AvroSingleObjectEncodedToGenericDataRecordTypeConverter(builder.schemaReadOnlyRegistry.schemaResolver()))
        this.registerConverter(GenericDataRecordToAvroSingleObjectEncodedConverter(builder.schemaReadOnlyRegistry.schemaResolver()))
      }
    } else {
      builder.converter
    },
    logger = LoggerFactory.getLogger(AvroSerializer::class.java),
    specificRecordToSingleObjectConverter = DefaultSpecificRecordToSingleObjectSchemaChangingConverter(
      schemaResolver = builder.schemaReadOnlyRegistry.schemaResolver()
    ),
    specificRecordToGenericDataRecordConverter = DefaultSpecificRecordToGenericDataRecordChangingSchemaConverter(
      schemaResolver = builder.schemaReadOnlyRegistry.schemaResolver()
    )
  )

  val genericRecordEncoder = DefaultGenericDataRecordToSingleObjectEncoder()


  override fun classForType(type: SerializedType): Class<*> {
    return if (SimpleSerializedType.emptyType() == type) {
      Void::class.java
    } else {
      ClassUtils.forName(type.name)
    }
  }

  override fun typeForClass(type: Class<*>?): SerializedType {

    if (type == null || Void.TYPE == type || Void::class.java == type) {
      return SimpleSerializedType.emptyType()
    }
      // FIXME; checked with "is SpecificRecord" ... not against type
//    else if (type ==  SpecificRecordBase::class.java) {
//      val schema = type.schema.find(schemaReadOnlyRegistry = schemaReadOnlyRegistry)
//      return SchemaSerializedType(schema)
//    } else if (type is GenericData.Record) {
//      val schema = type.schema.find(schemaReadOnlyRegistry = schemaReadOnlyRegistry)
//      return SchemaSerializedType(schema)
//    }
    else {
      return SimpleSerializedType(type.name, revisionResolver.revisionOf(type))
    }
  }

  override fun getConverter(): Converter = converter


  /*
   * Support GenericDataRecord as intermediate format (see registered converters for them)
   */
  override fun <T : Any> canSerializeTo(expectedRepresentation: Class<T>): Boolean =
    GenericData.Record::class.java == expectedRepresentation ||
      converter.canConvert(ByteArray::class.java, expectedRepresentation)

  override fun <T : Any> serialize(data: Any, expectedRepresentation: Class<T>): SerializedObject<T> {
    if (data is MetaData) {
      return metaDataSerializer.serialize(data, expectedRepresentation)
    }
    if (data is GenericData.Record) {
      return try {
        val schema = data.schema
        val serializedBytes: AvroSingleObjectEncoded = genericRecordEncoder.encode(data)
        logger.debug("Serialized: {} to {}", data, serializedBytes.toHexString())

        val serializedContent: Any = if (expectedRepresentation == AvroSingleObjectEncoded::class.java) {
          // this is an optimization to convert directly into a bytearray (avro single object encoded format)
          serializedBytes
        } else {
          converter.convert(serializedBytes, expectedRepresentation)
        }
        @Suppress("UNCHECKED_CAST")
        SimpleSerializedObject(
          serializedContent,
          expectedRepresentation as Class<Any>,
          //typeForClass(ObjectUtils.nullSafeTypeOf(data))
          SimpleSerializedType(schema.fullName, schema.revision)
        ) as SerializedObject<T>
      } catch (e: Exception) {
        throw SerializationException("Unable to serialize object into $expectedRepresentation", e)
      }
    }

    return try {
      require(data is SpecificRecordBase) { "Currently, data must be subtype of SpecificRecordBase, was: ${data.javaClass}" }

      val serializedBytes: AvroSingleObjectEncoded = specificRecordToSingleObjectConverter.encode(data)
      logger.debug("Serialized: {} to {}", data, serializedBytes.toHexString())

      val serializedContent: Any = if (expectedRepresentation == AvroSingleObjectEncoded::class.java) {
        // this is an optimization to convert directly into a bytearray (avro single object encoded format)
        serializedBytes
      } else {
        converter.convert(serializedBytes, expectedRepresentation)
      }
      @Suppress("UNCHECKED_CAST")
      SimpleSerializedObject(
        serializedContent,
        expectedRepresentation as Class<Any>,
        typeForClass(ObjectUtils.nullSafeTypeOf(data))
      ) as SerializedObject<T>
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
          // we run into this branch if the byte array was converted and manipulated on the level of the intermediate representation
          // in this case the format is GenericData.Record
          (specificRecordToGenericDataRecordConverter.decode(serializedObject.data as GenericData.Record) as T)
            .apply {
              logger.debug("deserialized: ${(serializedObject.data as GenericData.Record)} to $this")
            }
        }
        else -> {
          val bytesSerialized = converter.convert(serializedObject, AvroSingleObjectEncoded::class.java)
          (specificRecordToSingleObjectConverter.decode(bytesSerialized.data) as T)
            .apply {
              logger.debug("deserialized: ${(bytesSerialized.data as ByteArray).toHexString()} to $this")
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
    var schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry = AvroAdapterDefault.inMemorySchemaRegistry()

    fun revisionResolver(revisionResolver: RevisionResolver) = apply {
      this.revisionResolver = revisionResolver
    }

    fun converter(converter: Converter) = apply {
      this.converter = converter
    }

    fun schemaRegistry(schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry) = apply {
      this.schemaReadOnlyRegistry = schemaReadOnlyRegistry
    }

    fun build() = AvroSerializer(this)
  }
}
