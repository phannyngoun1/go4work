package com.dream.ticket.usecase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.dream.ticket.usecase.TicketAggregateUseCase.Protocol.{CreateTicketRequest, TicketResponse}

trait TicketAggregateFlows {

  def createTicketFlow: Flow[CreateTicketRequest, TicketResponse, NotUsed]
}
