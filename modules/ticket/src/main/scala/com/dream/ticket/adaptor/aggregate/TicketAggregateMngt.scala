package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging, Props}
import com.dream.ticket.usecase.TicketAggregateReadModelCase

object TicketAggregateMngt {
  def props( ticketAggregateReadModelCase: TicketAggregateReadModelCase): Props = Props(new TicketAggregateMngt(ticketAggregateReadModelCase))

  def name: String = "ticketAggregateMngt"

}

class TicketAggregateMngt(ticketAggregateReadModelCase: TicketAggregateReadModelCase) extends Actor with ActorLogging {

  override def receive: Receive = {
    case event:  TicketEvent => ticketAggregateReadModelCase.execute(event)
  }
}
