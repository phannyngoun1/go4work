package com.dream.workflow.domain

import java.util.UUID

import com.dream.workflow.domain.Flow.{ActivityHis, BaseActivity}
import com.dream.workflow.domain.ProcessInstance.Task


object ProcessInstance {

  case class Task(
    participants: List[Participant],
    //Priority
    activity: BaseActivity
  )

}

case class ProcessInstance(
  id: UUID,
  code: String,
  contentId: UUID,
  submitter: Participant,
  task: Task,
  activityHis: Seq[ActivityHis] = Seq.empty
)