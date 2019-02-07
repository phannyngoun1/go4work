package com.dream.workflow.entity.processinstance

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}
import com.dream.workflow.domain.ProcessInstance.InstError
import com.dream.workflow.domain._

object ProcessInstanceProtocol {

  sealed trait ProcessInstanceCmdRequest extends CmdRequest {
    def id: UUID
  }

  sealed trait TaskCmdRequest extends CmdRequest

  sealed trait ProcessInstanceCmdResponse extends CmdResponse

  sealed trait TaskCmdResponse extends CmdResponse

  case class CreatePInstCmdRequest(
    id: UUID,
    flowId: UUID,
    folio: String,
    contentType: String,
    activity: BaseActivity,
    action: BaseAction,
    by: Participant,
    description: String,
    destinations: List[Participant],
    nextActivity: BaseActivity,
    todo: String
  ) extends ProcessInstanceCmdRequest

  trait CreatePInstCmdResponse extends ProcessInstanceCmdResponse

  case class CreatePInstCmdSuccess(id: UUID) extends CreatePInstCmdResponse
  case class CreatePInstCmdFailed(id: UUID, error: InstError) extends CreatePInstCmdResponse

  case class GetPInstCmdRequest(
    id: UUID
  ) extends ProcessInstanceCmdRequest

  case class GetPInstCmdSuccess(
    processInstance: ProcessInstance
  ) extends  ProcessInstanceCmdResponse

  case class GetPInstCmdFailed(id: UUID, error: InstError) extends  ProcessInstanceCmdResponse

  case class PerformTaskCmdReq() extends TaskCmdRequest

  abstract class PerformTaskCmdRes() extends TaskCmdResponse


}
