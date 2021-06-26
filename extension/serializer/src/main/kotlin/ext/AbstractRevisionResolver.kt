package io.holixon.axon.avro.serializer.ext

import org.axonframework.serialization.RevisionResolver
import kotlin.reflect.KClass

abstract class AbstractRevisionResolver : RevisionResolver {

  fun revisionOf(payloadType: KClass<*>): String? = revisionOf(payloadType.java)
}
