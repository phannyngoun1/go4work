package com.dream.workflow.domain

import java.time.Instant
import java.util.UUID

import com.dream.workflow.domain.Participant.{ParticipantError, ParticipantEvent}
import play.api.libs.json.{Format, Json}

case class Team(name: String)

object Team {
  implicit val format: Format[Team] = Json.format
}

case class Department(name: String)

object Department {
  implicit val format: Format[Department] = Json.format
}

case class Company(name: String)

object Company {
  implicit val format: Format[Company] = Json.format
}

case class ParticipantTask(
  pInstId: UUID,
  description: String,
  activity: BaseActivity,
  actions: Seq[BaseAction],
  dateCreated: Instant = Instant.now(),
  dateCompleted: Option[Instant] = None,
  byActivityId: Option[UUID] = None
)

case class ParticipantAccess(participantId: UUID)

object Participant {

  sealed trait ParticipantError {
    val message: String
  }

  case class DefaultParticipantError(message: String) extends ParticipantError

  case class InvalidParticipantStateError(id: Option[UUID] = None) extends ParticipantError {

    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"

  }

  sealed trait ParticipantEvent

  case class ParticipantCreated(
    id: UUID,
    accountId: UUID,
    teamId: UUID,
    departmentId: UUID,
    propertyId: UUID
  ) extends ParticipantEvent

  case class TaskAssigned(
    pInstId: UUID,
    description: String,
    activity: BaseActivity,
    actions: Seq[BaseAction],
  ) extends ParticipantEvent

  case class TaskPerformed(
    activity: BaseActivity,
    action: BaseAction,
    activityId: UUID,
    by: UUID,
    date: Instant
  )
}

case class Participant(
  id: UUID,
  accountId: UUID,
  teamID: UUID,
  departmentId: UUID,
  propertyId: UUID,
  isActive: Boolean = true,
  isDeleted: Boolean = false,
  tasks: List[ParticipantTask] = List.empty,
  taskHist: List[ParticipantTask] = List.empty
) {

  def withEvent(event: ParticipantEvent): Either[ParticipantError, Participant] =
    event match {
      case Participant.TaskAssigned(pInstId, description, activity, actions) =>
        Right(copy(
          tasks =  ParticipantTask(
            pInstId = pInstId,
            description = description,
            activity = activity,
            actions = actions
          )  :: tasks
        ))
    }
}




