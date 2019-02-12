package com.dream.workflow.usecase

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.Participant.ParticipantError
import com.dream.workflow.domain.{BaseAction, BaseActivity}
import com.dream.workflow.usecase.port.ParticipantAggregateFlows

import scala.concurrent.{ExecutionContext, Future, Promise}

object ParticipantAggregateUseCase {

  object Protocol {

    sealed trait ParticipantCmdResponse

    sealed trait ParticipantCmdRequest

    sealed trait ParticipantErrorCmdRequest extends ParticipantCmdRequest {
      val id: UUID
      val error: ParticipantError
    }

    case class CreateParticipantCmdReq(
      id: UUID,
      accountId: UUID,
      teamId: UUID,
      departmentId: UUID,
      propertyId: UUID
    ) extends ParticipantCmdRequest

    sealed trait CreateParticipantCmdRes

    case class CreateParticipantCmdSuccess(
      id: UUID
    ) extends CreateParticipantCmdRes

    case class CreateParticipantCmdFailed(id: UUID, error: ParticipantError) extends CreateParticipantCmdRes with ParticipantErrorCmdRequest


    case class GetParticipantCmdReq(
      id: UUID
    ) extends ParticipantCmdRequest

    sealed trait GetParticipantCmdRes extends ParticipantCmdResponse

    case class GetParticipantCmdSuccess(
      id: UUID
    ) extends GetParticipantCmdRes

    case class GetParticipantCmdFailed(id: UUID, error: ParticipantError) extends GetParticipantCmdRes with ParticipantErrorCmdRequest


    case class AssignTaskCmdReq(
      id: UUID,
      pInstId: UUID,
      description: String,
      activity: BaseActivity,
      actions: Seq[BaseAction],
    ) extends ParticipantCmdRequest

    trait AssignTaskCmdRes extends ParticipantCmdResponse

    case class AssignTaskCmdSuccess() extends AssignTaskCmdRes

    case class AssignTaskCmdFailed(id: UUID, error: ParticipantError) extends AssignTaskCmdRes with ParticipantErrorCmdRequest

  }
}

class ParticipantAggregateUseCase(participantAggregateFlows: ParticipantAggregateFlows)(implicit system: ActorSystem) extends UseCaseSupport {

  import ParticipantAggregateUseCase.Protocol._
  import UseCaseSupport._

  implicit val mat: Materializer = ActorMaterializer()

  private val bufferSize: Int = 10

  def createParticipant(req: CreateParticipantCmdReq)(implicit ec: ExecutionContext): Future[CreateParticipantCmdRes] =
    offerToQueue(createParticipantQueue)(req, Promise())

  def getParticipant(req: GetParticipantCmdReq)(implicit ec: ExecutionContext): Future[GetParticipantCmdRes] =
    offerToQueue(getParticipantQueue)(req, Promise())

  def assignTask(req: AssignTaskCmdReq)(implicit ec: ExecutionContext): Future[AssignTaskCmdRes] =
    offerToQueue(assignTaskQueue)(req, Promise())

  private val createParticipantQueue: SourceQueueWithComplete[(CreateParticipantCmdReq, Promise[CreateParticipantCmdRes])] =
    Source.queue[(CreateParticipantCmdReq, Promise[CreateParticipantCmdRes])](bufferSize, OverflowStrategy.dropNew)
      .via(participantAggregateFlows.create.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

  private val getParticipantQueue: SourceQueueWithComplete[(GetParticipantCmdReq, Promise[GetParticipantCmdRes])] =
    Source.queue[(GetParticipantCmdReq, Promise[GetParticipantCmdRes])](bufferSize, OverflowStrategy.dropNew)
      .via(participantAggregateFlows.get.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

  private val assignTaskQueue: SourceQueueWithComplete[(AssignTaskCmdReq, Promise[AssignTaskCmdRes])] =
    Source.queue[(AssignTaskCmdReq, Promise[AssignTaskCmdRes])](bufferSize, OverflowStrategy.dropNew)
      .via(participantAggregateFlows.assignTask.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()
}
