package com.dream.workflow.domain

import java.util.UUID

import play.api.libs.json.{Format, Json}

object Item {

  sealed trait ItemError {
    val message: String
  }


  case class DefaultItemError(message: String) extends ItemError

  case class InvalidItemStateError(id: Option[UUID] = None) extends ItemError {

    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"
  }

}

case class Item(
  id: UUID,
  name: String,
  desc: String,
  workflowId: UUID,
  isActive: Boolean = true
)

sealed trait ItemEvent

case class ItemCreated(
  id: UUID,
  name: String,
  desc: String,
  workflowId: UUID
) extends ItemEvent

object ItemCreated {
  implicit val format: Format[ItemCreated] = Json.format
}