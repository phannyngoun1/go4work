package com.dream.ticket.adaptor.aggregate

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import com.dream.ticket.usecase.TicketAggregateUseCase.Protocol
import com.dream.ticket.usecase.port.TicketAggregateFlows

class TicketAggregateFlowsImpl(aggregateRef: ActorRef) extends TicketAggregateFlows {

  import Protocol._

  override def createTicketFlow: Flow[CreateTicketRequest, TicketResponse, NotUsed] =
    Flow[CreateTicketRequest]
      .mapAsync(1)(aggregateRef ? _ )
      .map {
        case _ => TicketCreatedSuccess(1)
      }


}
