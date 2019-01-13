package com.dream.ticket.adaptor.aggregate

import java.time.Instant

import com.dream.ticket.domain.Flow.BaseActivity
import com.dream.ticket.domain.{Flow, Participant, TicketContent}

object TicketAggregateProtocol {


  /***Ticket***/

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


  /****Flow****/

  sealed trait FlowCmdReq

  sealed trait FlowCmdRes

  case class InitialFlowCmdReq (flows: List[Flow]) extends FlowCmdReq

  case object InitialFlowCmdSuccess extends FlowCmdRes

  case object InitialFlowCmdFailed extends FlowCmdRes

  case class AddFlowCmdReq(flow: Flow) extends FlowCmdReq

  case object AddFlowCmdSuccess extends FlowCmdRes

  case object AddFlowCmdFailed extends FlowCmdRes

  case class UpdateFlowCmdReq(flow: Flow) extends FlowCmdReq

  case object UpdateFlowCmdSuccess extends FlowCmdRes

  case object UpdateFlowCmdFailed extends FlowCmdRes

  case class GetFlowCmdReq(id: Long) extends FlowCmdReq

  case class GetFlowCmdSuccess(flow: Flow) extends FlowCmdRes

  case object GetFlowCmdFailed extends FlowCmdRes




}
