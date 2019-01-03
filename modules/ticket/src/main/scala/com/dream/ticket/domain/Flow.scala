package com.dream.ticket.domain


object Flow {

  case class Activity(name: String)

  case class Action(name: String, onActivity: Activity, by: Participant )
}

case class Flow(

)
