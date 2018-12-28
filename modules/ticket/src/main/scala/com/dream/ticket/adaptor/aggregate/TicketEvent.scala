package com.dream.ticket.adaptor.aggregate

trait TicketEvent {
  val ticketId: Long
}

case class TicketCreated(
  override val ticketId: Long
) extends TicketEvent


