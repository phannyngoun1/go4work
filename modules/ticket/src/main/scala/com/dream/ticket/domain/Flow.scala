package com.dream.ticket.domain

import com.dream.ticket.domain.Flow._


object Flow {


  sealed abstract class FlowError(message: String)

  case class ActivityNotFound(message: String) extends FlowError(message)

  sealed trait BaseActivity {
    def name: String
  }

  sealed trait BaseAction

  sealed trait PayLoad

  sealed trait FlowParams

  sealed trait BaseActivityFlow {
    def name: String
  }


  case class ActivityHis(
    activity: BaseActivity,
    participant: Participant,
    action: BaseAction,
    payLoad: PayLoad
  )

  case class ActionFlow(action: BaseAction, activityFlow: BaseActivityFlow)


  case class ActivityFlow(activity: BaseActivity, participants: List[Participant], actionFlows: List[ActionFlow]) extends BaseActivityFlow {
    override def name: String = activity.na
  }

  case class NaActivityFlow() extends BaseActivityFlow {
    override def name: String = "N/A"
  }

  case class StayStillActivityFlow() extends BaseActivityFlow {
    override def name: String = "StayStill"
  }

  case class Activity(name: String) extends BaseActivity

  case class Action(name: String) extends BaseAction

  case class DoAction(action: BaseAction, onActivity: BaseActivity, by: Participant, flowParams: FlowParams)

//
//  case object DoneActivity extends BaseActivity {
//    override def name: String = "Done"
//  }
//
//  case object NoneActivity extends BaseActivity {
//    override def name: String = "None"
//  }

}

case class Flow(

  id: Long,
  activityFlow: ActivityFlow,
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


    quickAccessFlows.find(_.name == activity.name) match {
      case None => Left(ActivityNotFound(""))
      case Some(act: BaseActivityFlow )=> Right(act)
    }

  private def nextActivity(participant: Participant, action: BaseAction)(activityFlow: BaseActivityFlow): Either[FlowError, BaseActivityFlow] =

    activityFlow match {
      case act: ActivityFlow => act.actionFlows.find(_.action == action) match {
        case Some(act: BaseActivityFlow ) => Right(act)
        case _ => Left(ActivityNotFound(""))
      }
      case act: StayStillActivityFlow => Right(act)
      case  _ => Left(ActivityNotFound(""))
    }


}
