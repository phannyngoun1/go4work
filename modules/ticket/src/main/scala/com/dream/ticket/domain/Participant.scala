package com.dream.ticket.domain


case class Account()

case class Team()

case class Department()

case class Property()

case class Participant(
  id: Int,
  account: Account,
  team: Team,
  property: Property,
  isActive: Boolean = true,
  isDeleted: Boolean = false
)
