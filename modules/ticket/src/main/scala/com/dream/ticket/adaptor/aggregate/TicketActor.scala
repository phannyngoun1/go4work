package com.dream.ticket.adaptor.aggregate

import akka.actor.{Actor, ActorLogging, Props}
import com.dream.ticket.domain.Ticket


object TicketActor {

  def prop: Props = Props(new TicketActor)

  def name(ticketNo: String): String = s"ticket_${ticketNo}"

}

class TicketActor() extends Actor with ActorLogging {

  val ticket: Option[Ticket] = None

  override def receive: Receive = ???
}
