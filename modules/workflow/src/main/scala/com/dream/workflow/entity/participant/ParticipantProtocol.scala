package com.dream.workflow.entity.participant

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}
import com.dream.workflow.domain.{BaseAction, BaseActivity, Participant}
import com.dream.workflow.domain.Participant.ParticipantError

object ParticipantProtocol {

  sealed trait ParticipantCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait ParticipantCmdResponse extends CmdResponse

  case class CreateParticipantCmdReq(
    id: UUID,
    accountId: UUID,
    teamId: UUID,
    departmentId: UUID,
    propertyId: UUID
  ) extends ParticipantCmdRequest

  case class CreateParticipantCmdSuccess(
    id: UUID
  ) extends ParticipantCmdResponse

  case class CreateParticipantCmdFaild(
    id: UUID,
    error: ParticipantError

  ) extends ParticipantCmdResponse


  case class GetParticipantCmdReq(
    id: UUID
  ) extends ParticipantCmdRequest

  case class GetParticipantCmdSuccess(
    participant: Participant
  ) extends ParticipantCmdResponse

  case class GetParticipantCmdFaile(
    id: UUID,
    error: ParticipantError
  ) extends ParticipantCmdResponse


  case class AssignTaskCmdReq(
    id: UUID,
    pInstId: UUID,
    description: String,
    activity: BaseActivity,
    actions: Seq[BaseAction],
  ) extends ParticipantCmdRequest

  case class AssignTaskCmdSuccess(
    id: UUID
  ) extends ParticipantCmdResponse

  case class AssignTaskCmdFaild(
    id: UUID,
    error: ParticipantError

  ) extends ParticipantCmdResponse

}