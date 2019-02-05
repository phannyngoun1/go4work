package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging, Props}
import com.dream.ticket.usecase.TicketAggregateReadModelCase

import scala.concurrent.ExecutionContext

object TicketAggregateMngt {
  def props(ticketAggregateReadModelCase: TicketAggregateReadModelCase)(implicit ec: ExecutionContext): Props = Props(new TicketAggregateMngt(ticketAggregateReadModelCase)(ec))

  def name: String = "ticketAggregateMngt"

}

class TicketAggregateMngt(ticketAggregateReadModelCase: TicketAggregateReadModelCase)(implicit ec: ExecutionContext) extends Actor with ActorLogging {


  override def receive: Receive = {
    case event: TicketEvent => sender() ! ticketAggregateReadModelCase.execute(event)
    case _ => sender() ! "Ok"
  }
}
