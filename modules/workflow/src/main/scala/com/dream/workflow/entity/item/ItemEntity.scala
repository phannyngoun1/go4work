package com.dream.workflow.entity.item

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import com.dream.workflow.domain.FlowEvent.FlowCreated
import com.dream.workflow.domain.Item

object ItemEntity {

  def prop = Props(new ItemEntity)

  final val AggregateName  = "item"

  def name(uuId: UUID): String = uuId.toString

}

class ItemEntity  extends PersistentActor with ActorLogging {

  import ItemEntity._

  var state: Option[Item] = None

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: Item) =>
      state = Some(_state)

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) ⇒
      log.info(s"SaveSnapshotFailure: SaveSnapshotSuccess failed: $metadata, ${reason}")
//    case event: FlowCreated =>
//      state = applyState(event).toSomeOrThrow
    case RecoveryCompleted =>
      log.info(s"Recovery completed: $persistenceId")
    case _ => log.info("Other")

  }

  override def receiveCommand: Receive = {
    case cmd:
  }

  override def persistenceId: String = s"$AggregateName-${self.path.name}"

}
