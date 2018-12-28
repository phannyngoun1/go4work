package com.dream.ticket.domain

object TicketMessage {

  sealed trait Error {
    val message: String
  }

  sealed abstract class TicketError extends Error

  case class CreateTicketError(override val message: String) extends TicketError

}
