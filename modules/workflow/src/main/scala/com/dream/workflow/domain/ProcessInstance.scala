package com.dream.workflow.domain

import java.util.UUID

import com.dream.workflow.domain.ProcessInstance.{InstanceType, Task}


object ProcessInstance {

  sealed trait InstError {
    val message: String
  }

  case class DefaultInstError(message: String) extends InstError

  case class InvalidInstStateError(id: Option[UUID] = None) extends InstError {

    override val message: String = s"Invalid state${id.fold("")(id => s":id = ${id.toString}")}"
  }


  case class Task(
    description: String,
    participants: List[Participant],
    //Priority
    activity: BaseActivity
  )

  case class InstanceType(
    id: UUID,
    name: String,
    description: String
  )


  sealed trait ProcessInstanceEvent

  case class ProcessInstanceCreated(

    id: UUID,
    flowId: UUID,
    folio: String,
    contentType: String,
    activity: Activity,
    action: BaseAction,
    by: Participant,
    description: String,
    destinations: List[Participant],
    nextActivity: BaseActivity,
    todo: String
  ) extends ProcessInstanceEvent

}

case class ProcessInstance(
  id: UUID,
  flowId: UUID,
  folio: String,
  contentType: String,
  submitter: Participant,
  task: Task,
  activityHis: Seq[ActivityHis] = Seq.empty,
  isActive: Boolean = true
)