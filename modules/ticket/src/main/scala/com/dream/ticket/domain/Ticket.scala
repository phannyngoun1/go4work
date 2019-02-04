package com.dream.ticket.domain

import play.api.libs.json._

case class Ticket(

  id: Long,

  ticketNo: String,

  content: TicketContent,

  source: TicketSourceType,

  ticketType: TicketType
)

object Ticket {

  implicit val format: Format[Ticket] = Json.format

}
