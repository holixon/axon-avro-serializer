package io.holixon.axon.avro.common

import io.holixon.axon.avro.common.type.AvroPayloadAndSchema
import java.util.function.Function

interface SingleObjectDecoder : Function<AvroSingleObjectEncoded, AvroPayloadAndSchema>
