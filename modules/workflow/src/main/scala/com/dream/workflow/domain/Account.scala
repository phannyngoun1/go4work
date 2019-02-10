package com.dream.workflow.domain

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

}

case class Account(
  id: UUID,
  name: String,
  currentParticipantId: UUID,
  isActive: Boolean
)

