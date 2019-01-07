package com.dream.ticket.domain

import julienrf.json.derived
import play.api.libs.json.{Format, Json, OFormat}

sealed trait TicketContent {
  val subject: String
  val description: String
}

object TicketContent {
  implicit val jsonFormat: OFormat[TicketContent] = derived.oformat[TicketContent]()
}


case class DefaultContent(subject: String, description: String) extends TicketContent {
  require(!subject.isEmpty)
}

object DefaultContent {
  implicit val format: Format[DefaultContent] = Json.format
}


