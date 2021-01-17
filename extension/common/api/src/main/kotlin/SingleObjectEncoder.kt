package io.holixon.axon.avro.common

import io.holixon.axon.avro.common.type.AvroSchemaWithId
import java.util.function.BiFunction

interface SingleObjectEncoder : BiFunction<AvroSchemaWithId, ByteArray, AvroSingleObjectEncoded>
