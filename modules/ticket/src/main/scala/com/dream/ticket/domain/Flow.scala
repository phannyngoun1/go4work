package com.dream.ticket.domain

import com.dream.ticket.domain.Flow._


object Flow {


  sealed abstract class FlowError(message: String)

  case class ActivityNotFoundError(message: String) extends FlowError(message)

  sealed trait BaseActivity {
    def name: String

    override def equals(obj: Any): Boolean = obj match {
      case a: BaseActivity => name.equals(a.name)
      case _ => false
    }
  }

  sealed trait BaseAction {
    def name: String
  }

  sealed trait PayLoad

  sealed trait FlowParams

  sealed trait BaseActivityFlow {
    def activity: BaseActivity
  }

  case class ActivityHis(
    activity: BaseActivity,
    participant: Participant,
    action: BaseAction,
    payLoad: PayLoad
  )

  case class ActionFlow(action: BaseAction, activity: BaseActivity )


  case class ActivityFlow(activity: BaseActivity, participants: List[Participant], actionFlows: List[ActionFlow]) extends BaseActivityFlow


  /**
    * Predefined actions
    */


  case class StartAction() extends BaseAction {
    override def name: String = "Start"
  }

  case class DoneAction() extends BaseAction {
    override def name: String = "Done"
  }

  /**
    * Predefined activities
    */

  case class StartActivity() extends BaseActivity() {
    override def name: String = "Start"
  }


  case class CurrActivity() extends BaseActivity {
    override def name: String = "StayStill"
  }


  case class NaActivity() extends BaseActivity {
    override def name: String = "NaActivity"
  }

  case class DoneActivity() extends BaseActivity {
    override def name: String = "Done"
  }

  /**
    * Predefined activity flows
    */

  case class NaActivityFlow() extends BaseActivityFlow {
    override def activity: BaseActivity = NaActivity()
  }

  case class StayStillActivityFlow() extends BaseActivityFlow {
    override def activity: BaseActivity = CurrActivity()
  }

  case class DoneActivityFlow() extends BaseActivityFlow {
    override def activity: BaseActivity = DoneActivity()
  }

  case class Activity(name: String) extends BaseActivity

  case class Action(name: String) extends BaseAction


  case class DoAction(action: BaseAction, onActivity: BaseActivity, by: Participant, flowParams: Option[FlowParams] = None)

}

case class Flow(

  id: Long,
  initialActivity: BaseActivity,
  quickAccessFlows: Seq[BaseActivityFlow],
  isActive: Boolean = true

) {



  /**based one current activity + action + authorized participant => Next Activity flow
    */
  //TODO: check for authorized participant

  def nextActivity(doAction: DoAction, noneParticipantAllowed: Boolean): Either[FlowError, BaseActivityFlow ] = {

    for {
      currAct <- checkCurrentActivity(doAction.onActivity)
      nextAct <- nextActivity(doAction.by, doAction.action)(currAct)
    } yield nextAct

    //Right(NaActivityFlow())
  }

  private def checkCurrentActivity(activity: BaseActivity) : Either[FlowError, BaseActivityFlow] =

    quickAccessFlows.find(_.activity == activity ) match {
      case None => Left(ActivityNotFoundError(s"Current activity: ${activity.name} can't be found"))
      case Some(act: BaseActivityFlow )=> Right(act)
    }

  private def nextActivity(participant: Participant, action: BaseAction)(activityFlow: BaseActivityFlow): Either[FlowError, BaseActivityFlow] =

    activityFlow match {
      case act: ActivityFlow => act.actionFlows.find(_.action == action) match {
        case Some(af: ActionFlow) => quickAccessFlows.find(_.activity == af.activity) match {
          case Some(value: StayStillActivityFlow) => Right(activityFlow)
          case Some(value) => Right(value)
          case _  => Left(ActivityNotFoundError(s"Next activity cannot found by action: ${action.name}; current activity ${activityFlow.activity.name}"))
        }
        case _ => Left(ActivityNotFoundError(s"Next activity cannot found by action: ${action.name}; current activity ${activityFlow.activity.name}"))
      }
      case act: StayStillActivityFlow => Right(act)
      case  _ => Left(ActivityNotFoundError(""))
    }


}
