package com.dream.ticket.domain

import java.time.Instant

import com.dream.ticket.domain.Flow.{ActivityHis, BaseActivity, DoAction}
import play.api.libs.json._

case class Ticket(

  id: Long,

  ticketNo: String,

  content: TicketContent,

  source: TicketSourceType,

  ticketType: TicketType,

  submitter: Participant,

  assignedTo: Participant,

  modifiedBy: Option[Long] = None,

  createdAt: Instant = Instant.now(),

  modifiedAt: Option[Instant] = None,

  currActivity: BaseActivity,

  activityHis: Seq[ActivityHis] = Seq.empty

)

object Ticket {

  //implicit val format: Format[Ticket] = Json.format

}
