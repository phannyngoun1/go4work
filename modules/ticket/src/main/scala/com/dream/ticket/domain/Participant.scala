package com.dream.ticket.domain

import play.api.libs.json.{Format, Json}


case class Account(name: String)

object Account {
  implicit val format: Format[Account] = Json.format
}

case class Team(name: String)

object Team {
  implicit val format: Format[Team] = Json.format
}

case class Department(name: String)

object Department {
  implicit val format: Format[Department] = Json.format
}

case class Company(name: String)

object Company {
  implicit val format: Format[Company] = Json.format
}

case class Participant(
  id: Int,
  account: Account,
  team: Team,
  department: Department,
  company: Company,
  isActive: Boolean = true,
  isDeleted: Boolean = false
)

object Participant {
  implicit val format: Format[Participant] = Json.format
}
