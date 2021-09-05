package io.holixon.axon.avro.serializer

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.converter.GenericDataRecordToSpecificRecordConverter
import io.holixon.avro.adapter.api.decoder.SingleObjectToSpecificRecordDecoder
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.DefaultSchemaStore
import io.holixon.avro.adapter.common.converter.DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter
import io.holixon.avro.adapter.common.converter.SchemaResolutionSupport
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToSpecificRecordDecoder
import io.holixon.axon.avro.serializer.converter.AvroSingleObjectEncodedToGenericDataRecordTypeConverter
import io.holixon.axon.avro.serializer.converter.GenericDataRecordToAvroSingleObjectEncodedConverter
import io.holixon.axon.avro.serializer.ext.TypeExt.isAvroSingleObjectEncoded
import io.holixon.axon.avro.serializer.ext.TypeExt.isUnknown
import io.holixon.axon.avro.serializer.fn.SchemaTypeSerializer
import io.holixon.axon.avro.serializer.metadata.AvroMetaData
import io.holixon.axon.avro.serializer.metadata.AvroMetaDataExt
import io.holixon.axon.avro.serializer.metadata.AvroMetaDataExt.convertFromAvro
import io.holixon.axon.avro.serializer.metadata.AvroMetaDataExt.toAvroMetaDataObject
import io.holixon.axon.avro.serializer.revision.SchemaBasedRevisionResolver
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils
import org.axonframework.messaging.MetaData
import org.axonframework.serialization.*
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Serializer implementation that uses Avro Single Object format for serialization.
 */
class AvroSerializer private constructor(
  /**
   * Gets the revision from class. Only works for SpecificRecordBase, GenericData.Record does not have schema on class.
   */
  private val revisionResolver: RevisionResolver,

  /**
   * Serializer for GenericData.Record.
   */
  private val genericDataRecordSerializer: SchemaTypeSerializer<GenericData.Record>,

  /**
   * Serializer for SpecificRecordBase.
   */
  private val specificRecordSerializer: SchemaTypeSerializer<SpecificRecordBase>,

  /**
   * Axon converters, typically this is a [ChainingConverter] that contains SPI converters for base types and specific [GenericData.Record] converters for [IntermediateEventRepresentation] during upcasting.
   */
  private val converter: Converter,

  /**
   * Pass a logger instance to trace processing.
   */
  private val logger: Logger,

  /**
   * Converts a generic record to a specific record.
   */
  private val genericDataRecordToSpecificRecordConverter: GenericDataRecordToSpecificRecordConverter,

  /**
   * Decode singleOnject bytes to specific record.
   */
  private val specificRecordDecoder: SingleObjectToSpecificRecordDecoder
) : Serializer {

  companion object {

    /**
     * Creates a [Builder] instance to configure the serializer.
     */
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

    /**
     * Secondary constructor.
     */
    operator fun invoke(builder: Builder): AvroSerializer {
      val schemaResolver = AvroMetaDataExt.compositeSchemaRegistry(builder.schemaReadOnlyRegistry).schemaResolver()
      val schemaResolutionSupport = SchemaResolutionSupport(
        schemaResolver,
        builder.decoderSpecificRecordClassResolver,
        builder.schemaIncompatibilityResolver
      )

      val converter = if (builder.converter is ChainingConverter) {
        // use GenericData.Record as intermediate representation.
        // register ByteArray to GenericData.Record converter
        // register GenericData.Record to ByteArray converter
        (builder.converter as ChainingConverter).apply {
          this.registerConverter(AvroSingleObjectEncodedToGenericDataRecordTypeConverter(schemaResolver))
          this.registerConverter(GenericDataRecordToAvroSingleObjectEncodedConverter())
        }
      } else {
        builder.converter
      }

      return AvroSerializer(
        revisionResolver = builder.revisionResolver,
        genericDataRecordSerializer = SchemaTypeSerializer.genericRecordDataSerializer(converter),
        specificRecordSerializer = SchemaTypeSerializer.specificRecordDataSerializer(converter),
        converter = converter,
        logger = LoggerFactory.getLogger(AvroSerializer::class.java),
        genericDataRecordToSpecificRecordConverter = DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter(schemaResolutionSupport),
        specificRecordDecoder = DefaultSingleObjectToSpecificRecordDecoder(
          schemaStore = DefaultSchemaStore(schemaResolver),
          schemaResolutionSupport = schemaResolutionSupport
        )
      )
    }
  }

  override fun classForType(type: SerializedType): Class<*> = if (SimpleSerializedType.emptyType() == type) {
    Void::class.java
  } else {
    try {
      // if the class can not be found, it is unknown

      // FIXME: here we do just class for name ... while in the converter we have a specific schema compatibility check ... is that correct?
      ClassUtils.forName(type.name)
    } catch (e: ClassNotFoundException) {
      UnknownSerializedType::class.java
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
      converter.canConvert(AvroSingleObjectEncoded::class.java, expectedRepresentation)

  override fun <T : Any> serialize(data: Any, expectedRepresentation: Class<T>): SerializedObject<T> = when (data) {
    is MetaData -> SerializedMetaData(
      // transform to avroMetaData and recurse
      serialize(AvroMetaDataExt.convertToAvro(data), expectedRepresentation).data,
      expectedRepresentation
    )
    is GenericData.Record -> genericDataRecordSerializer.serialize(data, expectedRepresentation)
    is SpecificRecordBase -> specificRecordSerializer.serialize(data, expectedRepresentation)
    else -> throw IllegalArgumentException("cannot serialize $data to $expectedRepresentation")
  }

  override fun <S : Any, T : Any> deserialize(serializedObject: SerializedObject<S>): T? {
    if (SerializedType.emptyType() == serializedObject.type) {
      return null
    }
    if (SerializedMetaData.isSerializedMetaData(serializedObject) && serializedObject.contentType.isAvroSingleObjectEncoded()) {
      // use recursion, then convert
      val avroMetaData: AvroMetaData = deserialize(toAvroMetaDataObject(serializedObject))!!
      @Suppress("UNCHECKED_CAST")
      return convertFromAvro(avroMetaData) as T
    }

    val type: Class<*> = classForType(serializedObject.type)
    if (type.isUnknown()) {
      @Suppress("UNCHECKED_CAST")
      return UnknownSerializedType(this, serializedObject) as T
    }

    return try {
      @Suppress("UNCHECKED_CAST")
      when (serializedObject.contentType) {
        GenericData.Record::class.java -> {
          // we run into this branch if the byte array was converted and manipulated on the level of the intermediate representation
          // in this case the format is GenericData.Record
          (genericDataRecordToSpecificRecordConverter.convert(serializedObject.data as GenericData.Record) as T)
            .apply {
              logger.debug("deserialized: ${(serializedObject.data as GenericData.Record)} to $this")
            }
        }
        else -> {
          val bytesSerialized = converter.convert(serializedObject, AvroSingleObjectEncoded::class.java)
          val specificRecord: SpecificRecordBase = specificRecordDecoder.decode(bytesSerialized.data)
          (specificRecordDecoder.decode(bytesSerialized.data) as T)
            .apply {
              logger.debug("deserialized: ${(bytesSerialized.data as ByteArray).toHexString()} to $this")
            }
        }
      }
    } catch (e: Exception) {
      throw SerializationException("Error while deserializing object", e)
    }
  }

  /**
   * Builder for the resolver.
   */
  class Builder {
    /**
     * The Revision resolver to determine (schema) revision of message.
     * @default [SchemaBasedRevisionResolver]
     */
    var revisionResolver: RevisionResolver = SchemaBasedRevisionResolver()
      private set

    /**
     * The axon converter.
     * @default [ChainingConverter] using classloader SPI.
     */
    var converter: Converter = ChainingConverter()
      private set

    /**
     * Access to schemas.
     * @default [io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry].
     */
    var schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry = AvroAdapterDefault.inMemorySchemaRegistry()
      private set

    /**
     * Resolves class for schema.
     * @default [AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver] - using [ClassUtils.forName].
     */
    var decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver =
      AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver
      private set

    /**
     * How to determine if the writer schema encoded in the stored bytes is compatible to the reader schema found on the class path.
     * @default [AvroAdapterDefault.defaultSchemaCompatibilityResolver]
     */
    var schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
      private set

    fun revisionResolver(revisionResolver: RevisionResolver) = apply {
      this.revisionResolver = revisionResolver
    }

    /**
     * Sets the converter.
     */
    fun converter(converter: Converter) = apply {
      this.converter = converter
    }

    /**
     * Sets the (read-only) schema registry.
     */
    fun schemaRegistry(schemaReadOnlyRegistry: AvroSchemaReadOnlyRegistry) = apply {
      this.schemaReadOnlyRegistry = schemaReadOnlyRegistry
    }

    /**
     * Sets class resolver for specific record.
     */
    fun decoderSpecificRecordClassResolver(decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver) =
      apply {
        this.decoderSpecificRecordClassResolver = decoderSpecificRecordClassResolver
      }

    /**
     * Sets resolver for schema incompatibilities.
     */
    fun schemaIncompatibilityResolver(schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver) = apply {
      this.schemaIncompatibilityResolver = schemaIncompatibilityResolver
    }

    /**
     * Build the serializer.
     */
    fun build() = AvroSerializer(this)
  }

}
