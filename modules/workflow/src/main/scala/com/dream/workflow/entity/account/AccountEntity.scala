package com.dream.workflow.entity.account

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import cats.implicits._
import com.dream.common.EntityState
import com.dream.workflow.domain.Account._
import com.dream.workflow.domain.{Account}

object AccountEntity {

  final val AggregateName = "acc"

  def prop = Props(new AccountEntity)

  def name(uuId: UUID): String = uuId.toString

  implicit class EitherOps(val self: Either[AccountError, Account]) {
    def toSomeOrThrow: Option[Account] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }
}

class AccountEntity extends PersistentActor with ActorLogging with EntityState[AccountError,Account]{

  import AccountEntity._

  var state: Option[Account] = None

  private def applyState(event: AccountCreated): Either[AccountError, Account] =
    Either.right(
      Account(
        event.id,
        event.name,
        event.currentParticipantId,
        true
      )
    )

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: Account) =>
      println(s"SnapshotOffer ${_state}")
      state = Some(_state)

    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) ⇒
      log.debug(s"SaveSnapshotFailure: SaveSnapshotSuccess failed: $metadata, ${reason}")
    case event: AccountCreated=>
      println(s"replay event: $event")
      state = applyState(event).toSomeOrThrow
    case RecoveryCompleted =>
      println(s"Recovery completed: $persistenceId")
    case _ => log.debug("Other")

  }

  override def receiveCommand: Receive = {
    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  override def persistenceId: String =  s"$AggregateName-${self.path.name}"

  override protected def foreachState(f: Account => Unit): Unit =
      Either.fromOption(state, InvalidAccountStateError()).filterOrElse(_.isActive, InvalidAccountStateError()).foreach(f)

  override protected def mapState(f: Account => Either[AccountError, Account]): Either[AccountError, Account] =
    for {
      state    <- Either.fromOption(state, InvalidAccountStateError())
      newState <- f(state)
    } yield newState
}
