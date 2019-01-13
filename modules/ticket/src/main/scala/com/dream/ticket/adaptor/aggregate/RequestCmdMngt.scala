package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.dream.ticket.adaptor.aggregate.TicketAggregateProtocol.{AddFlowCmdReq, TicketCmdReq}


object RequestCmdMngt {
  def prop(implicit system: ActorSystem) = Props(new RequestCmdMngt(system))

}



class RequestCmdMngt(system: ActorSystem) extends Actor with  ActorLogging {

  val flowActor = system.actorOf(FlowActor.prop, FlowActor.name)

  override def receive: Receive = {
    case cmd: AddFlowCmdReq => flowActor forward cmd
    case cmd: TicketCmdReq =>
  }
}
