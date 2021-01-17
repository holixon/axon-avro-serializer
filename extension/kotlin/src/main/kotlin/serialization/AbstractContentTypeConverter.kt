package io.holixon.extensions.kotlin.serialization

import org.axonframework.serialization.ContentTypeConverter
import kotlin.reflect.KClass

/**
 * @param <S> type of source
 * @param <T> type of target
 */
abstract class AbstractContentTypeConverter<S : Any, T : Any>(
  protected val expectedSourceType: Class<S>,
  protected val targetType: Class<T>
) : ContentTypeConverter<S, T> {

  constructor(expectedSourceType: KClass<S>, targetType: KClass<T>) : this(expectedSourceType.java, targetType.java)

  override fun targetType(): Class<T> = targetType

  override fun expectedSourceType() : Class<S> = expectedSourceType
}
