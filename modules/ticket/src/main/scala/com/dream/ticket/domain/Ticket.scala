package com.dream.ticket.domain

import com.dream.ticket.domain.Flow.{ActivityHis, BaseActivity}
import play.api.libs.json._

case class Ticket(

  id: Long,

  ticketNo: String,

  content: TicketContent,

  source: TicketSourceType,

  ticketType: TicketType,

  submitter: Participant,

  assignedTo: Participant,

  currActivity: BaseActivity,

  activityHis: Seq[ActivityHis] = Seq.empty

)

object Ticket {

  implicit val format: Format[Ticket] = Json.format

}
