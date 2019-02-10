package com.dream.workflow.domain

import java.time.Instant
import java.util.UUID

object Account {

  sealed trait AccountEvent

  sealed trait AccountError {
    val message: String
  }

  case class AccountCreated(
    id: UUID,
    name: String,
    currentParticipantId: UUID
  ) extends AccountEvent

  case class InvalidAccountStateError(id: Option[UUID] = None) extends AccountError {
    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"
  }

  case class ParticipantHis(
    currParticipantId: UUID,
    disassociateDate: Instant
  )

}

case class Account(
  id: UUID,
  name: String,
  currentParticipantId: UUID,
  isActive: Boolean,
  currParticipantId: Option[UUID] = None,
  participantHist: List[UUID]
)

