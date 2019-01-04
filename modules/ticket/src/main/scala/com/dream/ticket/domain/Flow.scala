package com.dream.ticket.domain

import com.dream.ticket.domain.Flow.{Activity, DoAction}


object Flow {


  case class Activity(name: String, nextActivities: Map[Action, Activity])

  case class Action(name:String)

  case class DoAction(action: Action, onActivity: Activity, by: Participant )

}

case class Flow(
  id: Long,
  activity: Activity,
  isActive: Boolean = true

) {
  def nextActivity(doAction: DoAction): Activity = ???
}
