package com.dream.workflow.entity.workflow

import java.util.UUID

import akka.Done
import akka.actor.{ActorLogging, Props}
import akka.persistence._
import cats.implicits._
import WorkflowProtocol.CreateWorkflowCmdRequest
import com.dream.workflow.domain.FlowEvent.FlowCreated
import com.dream.workflow.domain.{Workflow, WorkflowError}
import com.dream.workflow.entity.processinstance.ProcessInstanceEntity

object WorkflowEntity {

  def prop = Props(new ProcessInstanceEntity)

  final val AggregateName  = "work_flow"

  def name(uuId: UUID): String = uuId.toString

  implicit class EitherOps(val self: Either[WorkflowError, Workflow]) {
    def toSomeOrThrow: Option[Workflow] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }

}

class WorkflowEntity extends PersistentActor with ActorLogging {

  import WorkflowEntity._
  var state: Option[Workflow] = None

  private def applyState(event: FlowCreated): Either[WorkflowError, Workflow] =
    Either.right(
      Workflow(
        event.id,
        event.initialActivityName,
        event.flowList,
        true
      )
    )

  override def receiveRecover: Receive = {

    case SnapshotOffer(_, _state: Workflow) =>
      state = Some(_state)
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) ⇒
      log.info(s"SaveSnapshotFailure: SaveSnapshotSuccess failed: $metadata, ${reason}")
    case event: FlowCreated =>
      state = applyState(event).toSomeOrThrow
    case RecoveryCompleted =>
      log.info(s"Recovery completed: $persistenceId")
    case _ => log.info("Other")
  }

  override def receiveCommand: Receive = {
    case cmd: CreateWorkflowCmdRequest => persist(FlowCreated(cmd.id, cmd.initialActivityName, cmd.flowList)) { event =>
      state = applyState(event).toSomeOrThrow
      sender() ! Done
    }
    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  override def persistenceId: String = s"$AggregateName-${self.path.name}"



}
