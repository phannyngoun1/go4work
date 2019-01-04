package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging}
import com.dream.ticket.domain.Flow

object FlowActor {

}

class FlowActor() extends Actor with ActorLogging {


  var state: Option[Flow] = None


  override def receive: Receive = ???

  private def initial(flow: Flow) =  {
    state = Some(flow)
  }


}
