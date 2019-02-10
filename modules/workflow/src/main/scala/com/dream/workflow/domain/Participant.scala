package com.dream.workflow.domain

import java.util.UUID

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

object Participant {

  sealed trait ParticipantError {
    val message: String
  }

  case class DefaultParticipantError(message: String) extends ParticipantError

  case class InvalidParticipantStateError(id: Option[UUID] = None) extends ParticipantError {

    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"

  }
}

case class Participant(
  id: Int,
  accountId: UUID,
  team: Team = Team("test"),
  department: Department = Department("test"),
  company: Company = Company("Test"),
  isActive: Boolean = true,
  isDeleted: Boolean = false
)


