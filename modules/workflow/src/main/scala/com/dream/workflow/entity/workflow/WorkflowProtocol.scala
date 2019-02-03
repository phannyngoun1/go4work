package com.dream.workflow.entity.workflow

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}
import com.dream.workflow.domain.Workflow.BaseActivityFlow

object WorkflowProtocol {

  sealed trait WorkFlowCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait WorkFlowCmdResponse extends CmdResponse {
    val id: UUID
  }

  case class CreateWorkflowCmdRequest(
    id: UUID,
    initialActivityName: String,
    flowList: Seq[BaseActivityFlow]
  ) extends WorkFlowCmdRequest

}
