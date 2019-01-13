package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging, Props}
import com.dream.ticket.adaptor.aggregate.TicketAggregateProtocol._
import com.dream.ticket.domain.Flow

object FlowActor {

  def prop: Props = Props(new FlowActor())

  def name = "FlowMngt"
}

class FlowActor() extends Actor with ActorLogging {

  var flows: List[Flow] = List.empty

  override def receive: Receive = {
    case InitialFlowCmdReq(values) =>
      initial(values)
      sender() ! InitialFlowCmdSuccess
    case AddFlowCmdReq(value) =>
      flows = add(value)
      sender() ! AddFlowCmdSuccess
  }


  private def initial(flows: List[Flow]) =  {
    this.flows = flows
  }

  private def add(flow: Flow): List[Flow] = {
    flow :: flows
  }

}
