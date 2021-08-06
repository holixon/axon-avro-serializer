package io.holixon.axon.avro.examples.bankaccount

import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
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
  fun findAll() = ResponseEntity.ok().body(
    schemaRegistry.findAll()
      .map {
        AvroSchemaInfoData(
          namespace = it.namespace,
          name = it.name,
          revision = it.revision
        )
      }
  )

}
