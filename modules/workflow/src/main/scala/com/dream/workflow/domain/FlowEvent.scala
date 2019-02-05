package com.dream.workflow.domain

import java.util.UUID

object FlowEvent {

  sealed trait FlowEvent {
    val id: UUID
  }

  case class FlowCreated(
    override val id: UUID,
    initialActivityName: String,
    flowList: Seq[BaseActivityFlow],
  ) extends FlowEvent

}
