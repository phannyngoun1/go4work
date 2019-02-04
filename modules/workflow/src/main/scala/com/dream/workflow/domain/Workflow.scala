package com.dream.workflow.domain

import java.time.Instant
import java.util.UUID

import com.dream.workflow.domain.Workflow._
import julienrf.json._
import play.api.libs.json._

object Workflow {

  sealed trait WorkflowError {
    val message: String
  }

  case class DefaultWorkflowError(message: String) extends WorkflowError

  case class ActivityNotFoundError(message: String) extends WorkflowError

  case class InvalidWorkflowStateError(id: Option[UUID] = None ) extends WorkflowError {
    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"
  }

  sealed trait BaseActivity {
    def name: String

    override def equals(obj: Any): Boolean = obj match {
      case a: BaseActivity => name.equals(a.name)
      case _ => false
    }
  }


  case object TestActivity extends BaseActivity {
    override def name: String = "test"
  }

  object BaseActivity {
    implicit val jsonFormat: OFormat[BaseActivity] = derived.oformat[BaseActivity]()
  }

  sealed trait BaseAction {
    def name: String
  }

  object BaseAction {
    implicit val jsonFormat: OFormat[BaseAction] = derived.oformat[BaseAction]()
  }

  sealed trait PayLoad {

  }

  object PayLoad {
    implicit val jsonFormat: OFormat[PayLoad] = derived.oformat[PayLoad]()
  }

  case class DefaultPayLoad(
    value: String
  ) extends PayLoad

  sealed trait Params

  object Params {
    implicit val jsonFormat: OFormat[Params] = derived.oformat[Params]()
  }

  case class DefaultFlowParams(value: String) extends Params

  sealed trait BaseActivityFlow {
    def activity: BaseActivity
  }

  object BaseActivityFlow {
    implicit val jsonFormat: OFormat[BaseActivityFlow] = derived.oformat[BaseActivityFlow]()
  }


  case class ActionHis(
    participant: Participant,
    action: BaseAction
  )

  object ActionHis {
    implicit val format: Format[ActionHis] = Json.format
  }

  case class ActivityHis(
    activity: BaseActivity,
    actionHis: Seq[ActionHis],
    payLoadId: UUID,
    actionDate: Instant = Instant.now()
  )

  object ActivityHis {
    implicit val format: Format[ActivityHis] = Json.format
  }

  case class ActionFlow(action: BaseAction, activity: BaseActivity )
  object ActionFlow {
    implicit val format: Format[ActionFlow] = Json.format
  }

  case class ActivityFlow(activity: BaseActivity, participants: List[Participant], actionFlows: List[ActionFlow]) extends BaseActivityFlow
  object ActivityFlow {
    implicit val format: Format[ActivityFlow] = Json.format
  }


  /**
    * Predefined actions
    */

  case class StartAction() extends BaseAction {
    override val name: String = "Start"
  }


  case class DoneAction() extends BaseAction {
    override val name: String = "Done"
  }

  /**
    * Predefined activities
    */

  case class StartActivity() extends BaseActivity() {
    override val name: String = "Start"
  }

  case class CurrActivity() extends BaseActivity {
    override val name: String = "StayStill"
  }


  case class NaActivity() extends BaseActivity {
    override val name: String = "NaActivity"
  }

  case class DoneActivity() extends BaseActivity {
    override val name: String = "Done"
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

  object Activity {
    implicit val format: Format[Activity] = Json.format
  }

  case class Action(name: String) extends BaseAction

  object Action {
    implicit val format: Format[Action] = Json.format
  }


  case class DoAction(
    instanceId: Option[UUID] = None,
    action: BaseAction,
    onActivity: BaseActivity,
    by: Participant,
    params: Option[Params] = None
  )


  object DoAction {
    implicit val format: Format[DoAction] = Json.format
  }
}


object WorkFlow {
  implicit val format: Format[Workflow] = Json.format
}

case class Workflow(
  id: UUID,
  initialActivityName: String,
  workflowList: Seq[BaseActivityFlow],
  isActive: Boolean = true

) {



  /**based one current activity + action + authorized participant => Next Activity flow
    */
  //TODO: check for authorized participant

  def nextActivity(doAction: DoAction, noneParticipantAllowed: Boolean): Either[WorkflowError, BaseActivityFlow ] = {

    for {
      currAct <- checkCurrentActivity(doAction.onActivity)
      nextAct <- nextActivity(doAction.by, doAction.action)(currAct)
    } yield nextAct

    //Right(NaActivityFlow())
  }

  private def checkCurrentActivity(activity: BaseActivity) : Either[WorkflowError, BaseActivityFlow] =

    workflowList.find(_.activity == activity ) match {
      case None => Left(ActivityNotFoundError(s"Current activity: ${activity.name} can't be found"))
      case Some(act: BaseActivityFlow )=> Right(act)
    }

  private def nextActivity(participant: Participant, action: BaseAction)(activityFlow: BaseActivityFlow): Either[WorkflowError, BaseActivityFlow] =

    activityFlow match {
      case act: ActivityFlow => act.actionFlows.find(_.action == action) match {
        case Some(af: ActionFlow) => workflowList.find(_.activity == af.activity) match {
          case Some(_: StayStillActivityFlow) => Right(activityFlow)
          case Some(value) => Right(value)
          case _  => Left(ActivityNotFoundError(s"Next activity cannot found by action: ${action.name}; current activity ${activityFlow.activity.name}"))
        }
        case _ => Left(ActivityNotFoundError(s"Next activity cannot found by action: ${action.name}; current activity ${activityFlow.activity.name}"))
      }
      case act: StayStillActivityFlow => Right(act)
      case  _ => Left(ActivityNotFoundError(""))
    }
}





