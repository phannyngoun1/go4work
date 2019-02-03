package com.dream.workflow.domain

import java.util.UUID

object Item {

}

case class Item (
  id: UUID,
  name: String,
  desc: String,
  workflowId: UUID
)

sealed trait ItemEvent

case class ItemCreated(
  id: UUID,
  name: String,
  desc: String,
  workflowId: UUID
) extends ItemEvent
