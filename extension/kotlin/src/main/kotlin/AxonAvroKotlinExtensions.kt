package io.holixon.extensions.kotlin

import org.axonframework.serialization.ContentTypeConverter

object AxonAvroKotlinExtensions {

  inline fun <reified S : Any, reified T : Any> contentTypeConverter(crossinline convertFn: (S) -> T) = object: ContentTypeConverter <S,T> {
    override fun expectedSourceType(): Class<S> = S::class.java
    override fun targetType(): Class<T> = T::class.java
    override fun convert(original: S): T = convertFn(original)
  }
}
