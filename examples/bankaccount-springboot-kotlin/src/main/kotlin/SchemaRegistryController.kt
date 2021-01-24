package io.holixon.axon.avro.examples.bankaccount

import io.holixon.axon.avro.common.AvroCommon
import io.holixon.axon.avro.common.AvroSchemaRegistry
import io.holixon.axon.avro.common.type.AvroSchemaInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schemas")
class SchemaRegistryController(
  private val schemaRegistry: AvroSchemaRegistry
) {

  @GetMapping("/")
  fun findAll(): ResponseEntity<List<AvroSchemaInfo>> = ResponseEntity.ok().body(
    schemaRegistry.findAll()
      .map { AvroCommon.AvroSchemaInfoData(it.context, it.name, it.revision) }
  )

}
