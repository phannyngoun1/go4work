package com.dream.ticket.domain

import julienrf.json.derived
import play.api.libs.json.{Format, Json, OFormat}

sealed trait TicketContent {
  def getSubject: String
}

object TicketContent {
  implicit val jsonFormat: OFormat[TicketContent] = derived.oformat[TicketContent]()
}


case class DefaultContent(subject: String, description: String) extends TicketContent {

  require(!subject.isEmpty)

  override def getSubject: String = subject

}

object DefaultContent {
  implicit val format: Format[DefaultContent] = Json.format
}


