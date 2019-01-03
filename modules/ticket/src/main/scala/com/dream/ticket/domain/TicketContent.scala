package com.dream.ticket.domain

object TicketContent {


}


sealed trait TicketContent{

  def getSubject: String

}



case class DefaultContent(subject: String, description: String) extends TicketContent {

  require(!subject.isEmpty)

  override def getSubject: String = subject

}



