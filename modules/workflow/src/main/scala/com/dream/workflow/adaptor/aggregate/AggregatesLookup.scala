package com.dream.workflow.adaptor.aggregate

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorRef}
import com.dream.workflow.adaptor.aggregate.ProcessInstanceProtocol.ProcessInstanceCmdRequest
import com.dream.workflow.adaptor.aggregate.WorkflowProtocol.WorkFlowCmdRequest

trait AggregatesLookup {

  implicit def context: ActorContext

  def forwardToEntityAggregate: Actor.Receive = {
    case cmd: ProcessInstanceCmdRequest =>
      context
        .child(ProcessInstanceEntity.name(cmd.id))
        .fold(createAndForwardProcessInstanceCmd(cmd, cmd.id))(forwardProcessInstanceCmd(cmd))

    case cmd: WorkFlowCmdRequest =>
      context
        .child(ProcessInstanceEntity.name(cmd.id))
        .fold(createAndForwardWorkflowCmd(cmd, cmd.id))(forwardWorkflowCmd(cmd))
  }


  private def forwardWorkflowCmd(cmd: WorkFlowCmdRequest)(ref: ActorRef): Unit = {
    ref forward cmd
  }

  private def createAndForwardWorkflowCmd(cmd: WorkFlowCmdRequest, uuId: UUID) = {
    createWorkflowAggregate(uuId) forward cmd
  }

  private def createWorkflowAggregate(uuId: UUID) =
    context.actorOf(ProcessInstanceEntity.prop, ProcessInstanceEntity.name(uuId  ) )


  /*Process instance*/
  private def forwardProcessInstanceCmd(cmd: ProcessInstanceCmdRequest)(ref: ActorRef): Unit = {
    ref forward cmd
  }

  private def createAndForwardProcessInstanceCmd(cmd: ProcessInstanceCmdRequest, uuId: UUID) = {
    createProcessInstanceAggregate(uuId) forward cmd
  }

  private def createProcessInstanceAggregate(uuId: UUID) =
    context.actorOf(ProcessInstanceEntity.prop, ProcessInstanceEntity.name(uuId  ) )

}
