package com.dream.ticket.adaptor.aggregate.tasks
import akka.actor.{Actor, ActorLogging, Props}

object CreateTicketContentTask extends Task {

  override def props(): Props = ???

  override def taskName: String = ???

  override def taskCode: String = ???
}

class CreateTicketContentTask extends Actor with  ActorLogging {

  override def receive: Receive = ???
}
