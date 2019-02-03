package com.dream.workflow.entity.item

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}

object ItemProtocol {

  sealed trait ItemCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait ItemCmdResponse extends CmdResponse {
  }

  case class NewItemCmdRequest(
    val id: UUID,
    name: String,
    desc: String,
    workflowId: UUID

  ) extends ItemCmdRequest


  case class NewItemCmdResponse(
    workflowId: UUID
  ) extends ItemCmdResponse


  case class NewItemCmdSuccess(
    id: UUID
  ) extends ItemCmdResponse

  case class NewItemCmdFailed(
    message: String
  ) extends ItemCmdResponse

  case class GetWorkflowId(
    id: UUID
  ) extends ItemCmdRequest


  case class GetWorkflowCmdSuccess(
    workflowId: UUID
  ) extends ItemCmdResponse

  case class GetWorkflowCmdFailed(
    workflowId: UUID
  ) extends ItemCmdResponse

}
