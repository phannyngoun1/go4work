package com.dream.ticket.domain

import com.dream.common.domain.Type
import enumeratum.values.{StringEnumEntry, _}

sealed abstract class TicketSourceType(val value: String, val name: String, val description: Option[String] = None) extends StringEnumEntry with Type

object TicketSourceType extends StringEnum[TicketSourceType] with StringPlayJsonValueEnum[TicketSourceType] {

  override def values = findValues

  case object Email extends TicketSourceType("Email", "Email")

  case object PhoneCall extends TicketSourceType("PhoneCall", "Phone Call")

  case object WebForm extends TicketSourceType("WebForm", "WebForm")

  case object WalkIn extends TicketSourceType("WalkIn", "Walk In")

}


sealed abstract class TicketType(val value: String, val name: String, val description: Option[String] = None) extends StringEnumEntry with Type

object TicketType extends StringEnum[TicketType] with StringPlayJsonValueEnum[TicketType] {

  override def values = findValues

  case object Incident extends TicketSourceType("Incident", "Incident")

  case object AskForInfo extends TicketSourceType("AskForInfo", "Ask for information")

  case object Request extends TicketSourceType("Request", "Request")

  case object Approval extends TicketSourceType("Approval", "Approval")

  case object Unidentified extends TicketSourceType("Unidentified", "Unidentified")

}

sealed abstract class PriorityType(val value: String, val name: String, val description: Option[String] = None) extends StringEnumEntry with Type

object PriorityType extends StringEnum[PriorityType] with StringPlayJsonValueEnum[PriorityType] {

  override def values = findValues

  case object Urgent extends TicketSourceType("Urgent", "Urgent")

  case object High extends TicketSourceType("High", "High")

  case object Medium extends TicketSourceType("Medium", "Medium")

  case object Normal extends TicketSourceType("Normal", "Normal")

  case object Low extends TicketSourceType("Low", "Low")


}


sealed abstract class PayLoadType(val value: String, val name: String, val description: Option[String] = None) extends StringEnumEntry with Type

object PayLoadType extends StringEnum[PriorityType] with StringPlayJsonValueEnum[PriorityType] {

  override def values = findValues


}