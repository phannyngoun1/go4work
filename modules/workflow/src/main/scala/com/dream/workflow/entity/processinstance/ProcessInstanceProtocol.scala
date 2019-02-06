package com.dream.workflow.entity.processinstance

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}
import com.dream.workflow.domain.ProcessInstance.InstError
import com.dream.workflow.domain._

object ProcessInstanceProtocol {

  sealed trait ProcessInstanceCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait ProcessInstanceCmdResponse extends CmdResponse

  case class CreatePInstCmdRequest(
    id: UUID,
    flowId: UUID,
    folio: String,
    contentType: String,
    activity: Activity,
    action: BaseAction,
    by: Participant,
    description: String,
    destinations: List[Participant],
    nextActivity: BaseActivity,
    todo: String
  ) extends ProcessInstanceCmdRequest

  case class CreatePInstCmdSuccess(id: UUID) extends ProcessInstanceCmdResponse
  case class CreatePInstCmdFailed(id: UUID, error: InstError) extends ProcessInstanceCmdResponse

  case class GetPInstCmdRequest(
    id: UUID
  ) extends ProcessInstanceCmdRequest

  case class GetPInstCmdSuccess(
    processInstance: ProcessInstance
  ) extends  ProcessInstanceCmdResponse

  case class GetPInstCmdFailed(id: UUID, error: InstError) extends  ProcessInstanceCmdResponse
}
