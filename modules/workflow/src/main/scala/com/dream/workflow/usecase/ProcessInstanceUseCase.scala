package com.dream.workflow.usecase

import akka.actor.ActorSystem
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.Flow.{DoAction, Params}
import com.dream.workflow.domain.Participant
import com.dream.workflow.usecase.ProcessInstanceUseCase.Protocol.WorkFlowActionCmdRequest
import com.dream.workflow.usecase.port.ProcessInstanceAggregateFlows


object ProcessInstanceUseCase {

  object Protocol {

    sealed trait ProcessInstanceCmdResponse
    sealed trait ProcessInstanceCmdRequest

    case class WorkFlowActionCmdRequest(
      doAction: DoAction
    ) extends ProcessInstanceCmdRequest

    case class CreateInstanceCmdRequest(
      itemID: Long,
      by: Participant,
      params: Option[Params] = None
    ) extends ProcessInstanceCmdRequest

  }

}

class ProcessInstanceUseCase(processInstanceAggregateFlows: ProcessInstanceAggregateFlows )(implicit system: ActorSystem)
  extends UseCaseSupport {
  import UseCaseSupport._
  import ProcessInstanceUseCase.Protocol._

  def startInstance(req: CreateInstanceCmdRequest) = {
    //get workflow if
    //get activities : current & next
    //trigger curr act task
    //create workflow instance
  }

  def takeAction(request: WorkFlowActionCmdRequest) = {

  }

}
