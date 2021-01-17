package io.holixon.axon.avro.common.ext

import java.util.function.BiFunction
import java.util.function.Function

object FunctionExt {
  operator fun <I : Any, O : Any> Function<I, O>.invoke(input: I): O = this.apply(input)
}

object BiFunctionExt {
  operator fun <I1 : Any, I2 : Any, O : Any> BiFunction<I1, I2, O>.invoke(input1: I1, input2: I2): O = this.apply(input1, input2)
}
