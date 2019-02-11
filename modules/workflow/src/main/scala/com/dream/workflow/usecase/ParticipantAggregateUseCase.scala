package com.dream.workflow.usecase

import java.util.UUID

import com.dream.workflow.domain.Participant.ParticipantError
import com.dream.workflow.domain.{BaseAction, BaseActivity}
import com.dream.workflow.usecase.port.ParticipantAggregateFlows


object ParticipantAggregateUseCase {

  object Protocol {
    sealed trait ParticipantCmdResponse
    sealed trait ParticipantCmdRequest
    sealed trait ParticipantErrorCmdRequest extends ParticipantCmdRequest {
      val id: UUID
      val error: ParticipantError
    }

    case class CreateParticipantCmdReq(
      id: UUID,
      accountId: UUID,
      teamId: UUID,
      departmentId: UUID,
      propertyId: UUID
    ) extends ParticipantCmdRequest

    sealed trait CreateParticipantCmdRes

    case class CreateParticipantCmdSuccess(
      id: UUID
    ) extends CreateParticipantCmdRes

    case class CreateParticipantCmdFailed(id: UUID, error: ParticipantError) extends CreateParticipantCmdRes with ParticipantErrorCmdRequest


    case class GetParticipantCmdReq(
      id: UUID
    ) extends ParticipantCmdRequest

    sealed trait GetParticipantCmdRes extends ParticipantCmdResponse

    case class GetParticipantCmdSuccess(
      id: UUID
    ) extends GetParticipantCmdRes

    case class GetParticipantCmdFailed(id: UUID, error: ParticipantError) extends GetParticipantCmdRes with ParticipantErrorCmdRequest


    case class AssignTaskCmdReq(
      id: UUID,
      pInstId: UUID,
      description: String,
      activity: BaseActivity,
      actions: Seq[BaseAction],
    ) extends ParticipantCmdRequest
    trait AssignTaskCmdRes extends ParticipantCmdResponse
    case class AssignTaskCmdSuccess() extends AssignTaskCmdRes
    case class AssignTaskCmdFailed(id: UUID, error: ParticipantError)  extends AssignTaskCmdRes with ParticipantErrorCmdRequest
  }

}

class ParticipantAggregateUseCase(participantAggregateFlows: ParticipantAggregateFlows) {

}
