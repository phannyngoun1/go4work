package com.dream.workflow.domain

import java.util.UUID

import com.dream.workflow.domain.Flow.{ActivityHis, BaseActivity}
import com.dream.workflow.domain.ProcessInstance.{InstanceType, Task}


object ProcessInstance {

  case class Task(
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
    uuId: UUID,

  ) extends ProcessInstanceEvent

}

case class ProcessInstance(
  id: UUID,
  code: String,
  instanceType: InstanceType,
  flowId: UUID,
  contentId: UUID,
  submitter: Participant,
  task: Task,
  activityHis: Seq[ActivityHis] = Seq.empty
)