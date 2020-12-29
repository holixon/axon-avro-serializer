package io.holixon.axon.avro.lib.test

object AxonAvroSerializerTestLib {

  fun loadResource(resName:String) = {}::class.java.getResource(resName.trailingSlash()).readText()
  fun loadArvoResource(resName:String) = loadResource("/avro/$resName.avsc")

  private fun String.trailingSlash() = if (startsWith("/")) this else "/$this"
}
