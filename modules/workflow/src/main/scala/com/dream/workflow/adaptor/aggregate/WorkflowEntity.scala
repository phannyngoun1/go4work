package com.dream.workflow.adaptor.aggregate

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotSuccess}
import com.dream.workflow.adaptor.aggregate.ProcessInstanceEntity.AggregateName
import com.dream.workflow.domain.Flow.FlowError
import com.dream.workflow.domain.FlowEvent.FlowCreated
import com.dream.workflow.domain.{Flow, WorkflowError}
import cats.implicits._


object WorkflowEntity {

  def prop = Props(new ProcessInstanceEntity)

  final val AggregateName  = "work_flow"

  def name(uuId: UUID): String = uuId.toString

  implicit class EitherOps(val self: Either[WorkflowError, Flow]) {
    def toSomeOrThrow: Option[Flow] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }

}

class WorkflowEntity extends PersistentActor with ActorLogging {

  var state: Option[Flow] = None

  private def applyState(event: FlowCreated): Either[FlowError, Flow] =
    Either.right(
      Flow(
        event.id,
        event.initialActivityName,
        event.flowList,
        true
      )
    )

  override def receiveRecover: Receive = {

    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case RecoveryCompleted =>
      log.debug(s"Recovery completed: $persistenceId")
  }

  override def receiveCommand: Receive = {

    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  override def persistenceId: String = s"$AggregateName-${self.path.name}"



}
