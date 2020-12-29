package io.holixon.extensions.kotlin.serialization

import org.axonframework.serialization.RevisionResolver
import kotlin.reflect.KClass

abstract class AbstractRevisionResolver : RevisionResolver {

  fun revisionOf(payloadType: KClass<*>): String? = revisionOf(payloadType.java)
}
