package com.dream.ticket.adaptor.aggregate

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object TicketAggregate {

  final val AggregateName  = "ticket"

  def name(ticketId: Long): String = ticketId.toString

}

class TicketAggregate extends PersistentActor with ActorLogging {

  import TicketAggregate._

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String =  s"$AggregateName-${self.path.name}"
}
