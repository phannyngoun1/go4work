package com.dream.workflow.entity.participant

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import cats.implicits._
import com.dream.common.EntityState
import com.dream.workflow.domain.Participant
import com.dream.workflow.domain.Participant._
import com.dream.workflow.entity.participant.ParticipantProtocol.{CreateParticipantCmdReq, CreateParticipantCmdSuccess, GetParticipantCmdReq, GetParticipantCmdSuccess}

object ParticipantEntity {

  final val AggregateName = "pp"

  def prop = Props(new ParticipantEntity)

  def name(uuId: UUID): String = uuId.toString

  implicit class EitherOps(val self: Either[ParticipantError,Participant]) {
    def toSomeOrThrow: Option[Participant] = self.fold(error => throw new IllegalStateException(error.message), Some(_))
  }
}

class ParticipantEntity extends PersistentActor with ActorLogging with EntityState[ParticipantError,Participant]{

  import ParticipantEntity._

  var state: Option[Participant] = None

  private def applyState(event: ParticipantCreated): Either[ParticipantError, Participant] =
    Either.right(
      Participant(
        id = event.id,
        accountId = event.accountId,
        teamID = event.teamId,
        departmentId = event.departmentId,
        propertyId = event.propertyId,
      )
    )

  override protected def foreachState(f: Participant => Unit): Unit =
    Either.fromOption(state, InvalidParticipantStateError()).filterOrElse(_.isActive, InvalidParticipantStateError()).foreach(f)

  override protected def mapState(f: Participant => Either[ParticipantError, Participant]): Either[ParticipantError, Participant] =
    for {
      state    <- Either.fromOption(state, InvalidParticipantStateError())
      newState <- f(state)
    } yield newState

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, _state: Participant) =>
      println(s"SnapshotOffer ${_state}")
      state = Some(_state)

    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveRecover: SaveSnapshotSuccess succeeded: $metadata")
    case SaveSnapshotFailure(metadata, reason) ⇒
      log.debug(s"SaveSnapshotFailure: SaveSnapshotSuccess failed: $metadata, ${reason}")
    case event: ParticipantCreated=>
      println(s"replay event: $event")
      state = applyState(event).toSomeOrThrow
    case RecoveryCompleted =>
      println(s"Recovery completed: $persistenceId")
    case _ => log.debug("Other")

  }

  override def receiveCommand: Receive = {

    case cmd: CreateParticipantCmdReq =>
      persist(ParticipantCreated(
        id = cmd.id,
        accountId = cmd.accountId,
        teamId = cmd.teamId,
        departmentId = cmd.departmentId,
        propertyId = cmd.propertyId
      )) { event =>

        applyState(event).toSomeOrThrow
        sender() ! CreateParticipantCmdSuccess(event.id)
      }

    case GetParticipantCmdReq(id) if equalsId(id)(state, _.id.equals(id)) =>
      foreachState{ state =>
        sender() ! GetParticipantCmdSuccess(state)
      }

    case SaveSnapshotSuccess(metadata) =>
      log.debug(s"receiveCommand: SaveSnapshotSuccess succeeded: $metadata")
  }

  override def persistenceId: String = s"$AggregateName-${self.path.name}"
}
