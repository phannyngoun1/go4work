package com.dream.ticket.domain

import play.api.libs.json.{Format, Json}

sealed trait TicketContent{

  def getSubject: String

}



case class DefaultContent(subject: String, description: String) extends TicketContent {

  require(!subject.isEmpty)

  override def getSubject: String = subject

}


object DefaultContent {
  implicit val format: Format[DefaultContent] = Json.format
}


