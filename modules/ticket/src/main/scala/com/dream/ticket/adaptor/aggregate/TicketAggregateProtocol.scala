package com.dream.ticket.adaptor.aggregate

import java.time.Instant

import com.dream.ticket.domain.Flow.BaseActivity
import com.dream.ticket.domain.{Participant, TicketContent}

object TicketAggregateProtocol {

  sealed trait TicketCmdReq {
    val id: Long
  }

  sealed trait TicketCmdRes {
    val id: Long
  }

  sealed abstract class TicketActionCmdReq(

  ) extends TicketCmdReq

  case class CreateTicketCmd(
    override val id: Long,
    content: TicketContent,
    currActivity: BaseActivity,

    routes: List[Participant],
    actionDate: Instant = Instant.now()

  ) extends TicketCmdReq



}
