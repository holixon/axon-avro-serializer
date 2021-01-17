package io.holixon.axon.avro.common.darwin

import io.holixon.axon.avro.common.SchemaResolver
import it.agilelab.darwin.common.Connector
import org.apache.avro.Schema
import scala.Function1
import scala.Option
import scala.Tuple2
import scala.collection.immutable.Seq

object AvroCommonDarwin {
  val connector = object : Connector {
    override fun fullLoad(): Seq<Tuple2<Any, Schema>> = TODO("Not implemented")
    override fun insert(schemas: Seq<Tuple2<Any, Schema>>?) = TODO("Not implemented")
    override fun findSchema(id: Long): Option<Schema> = TODO("Not implemented")
    override fun tableCreationHint(): String = TODO("Not implemented")
    override fun tableExists(): Boolean = TODO("Not implemented")
    override fun createTable() = TODO("Not implemented")
  }

  internal fun SchemaResolver.darwin(): Function1<Any, Option<Schema>> = Function1<Any, Option<Schema>> { id ->
    this.apply(id as Long)
      .map { it.schema }
      .map { Option.apply(it) }
      .orElse(Option.empty())
  }
}
