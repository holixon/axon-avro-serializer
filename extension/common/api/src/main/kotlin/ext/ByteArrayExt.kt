package io.holixon.axon.avro.common.ext

object ByteArrayExt {

  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   *
   * @return the hexadecimal string representation of the input byte array
   */
  fun ByteArray.toHexString() : String = this.joinToString(
    separator = " ",
    prefix = "[",
    postfix = "]"
  ) { "%02X".format(it) }
}
