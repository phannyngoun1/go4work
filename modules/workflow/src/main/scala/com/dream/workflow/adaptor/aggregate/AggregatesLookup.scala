package com.dream.workflow.adaptor.aggregate

import akka.actor.{Actor, ActorContext}
import com.dream.workflow.entity.item.ItemEntity
import com.dream.workflow.entity.item.ItemProtocol.ItemCmdRequest
import com.dream.workflow.entity.processinstance.ProcessInstanceEntity
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.ProcessInstanceCmdRequest
import com.dream.workflow.entity.workflow.WorkflowEntity
import com.dream.workflow.entity.workflow.WorkflowProtocol.WorkFlowCmdRequest

trait AggregatesLookup {

  implicit def context: ActorContext

  def forwardToEntityAggregate: Actor.Receive = {
    case cmd: ProcessInstanceCmdRequest =>
      context
        .child(ProcessInstanceEntity.name(cmd.id))
        .fold(
          context.actorOf(ProcessInstanceEntity.prop, ProcessInstanceEntity.name(cmd.id )) forward cmd
        )(_ forward cmd)

    case cmd: WorkFlowCmdRequest =>
      context
        .child(WorkflowEntity.name(cmd.id))
        .fold(
          context.actorOf(WorkflowEntity.prop, WorkflowEntity.name(cmd.id)) forward cmd
        )(_ forward cmd)
    case cmd: ItemCmdRequest =>
      context
      .child(ItemEntity.name(cmd.id))
      .fold (
        context.actorOf(ItemEntity.prop, ItemEntity.name(cmd.id)) forward cmd
      )(_ forward cmd)
  }

}
