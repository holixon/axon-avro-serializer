package io.holixon.axon.avro.serializer.upcaster

import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster

/**
 * Avro single event upcaster draft.
 */
class AvroSingleEventUpcaster : SingleEventUpcaster() {
  
  override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
    TODO("Not yet implemented")
  }

  override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
    TODO("Not yet implemented")
  }
}
