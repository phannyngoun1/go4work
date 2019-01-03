package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging}

object FlowActor {

  case class ActionTrigger(flowId: String, )

}

class FlowActor() extends Actor with ActorLogging {

  override def receive: Receive = ???
}
