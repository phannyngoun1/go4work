package com.dream.ticket.domain

import java.time.Instant

import play.api.libs.json._

case class Participant(

  agentId: Long,

  teamId: Long,

  deptId: Long

)

object Participant {
  implicit val format: Format[Participant] = Json.format
}

case class Ticket(

  id: Long,

  ticketNo: String,

  subject: String,

  description: String,

  source: TicketSourceType,

  ticketType: TicketType,

  submitter: Participant,

  assignedTo: Participant,

  modifiedBy: Option[Long] = None,

  createdAt: Instant = Instant.now(),

  modifiedAt: Option[Instant] = None
)

object Ticket {
  implicit val format: Format[Ticket] = Json.format
}
