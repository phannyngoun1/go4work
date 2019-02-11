package com.dream.workflow.adaptor.aggregate

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.dream.workflow.entity.participant.ParticipantProtocol._
import com.dream.workflow.usecase.ParticipantAggregateUseCase.Protocol
import com.dream.workflow.usecase.port.ParticipantAggregateFlows

import scala.concurrent.duration._
import scala.language.postfixOps

class ParticipantAggregateFlowsImpl(aggregateRef: ActorRef) extends ParticipantAggregateFlows {

  private implicit val to: Timeout = Timeout(2 seconds)

  override def create: Flow[Protocol.CreateParticipantCmdReq, Protocol.CreateParticipantCmdRes, NotUsed] =
    Flow[Protocol.CreateParticipantCmdReq]
      .map(req => CreateParticipantCmdReq(
        id = req.id,
        accountId = req.accountId,
        teamId = req.teamId,
        departmentId = req.departmentId,
        propertyId = req.propertyId
      ))
      .mapAsync(1)(aggregateRef ? _)
      .map {
        case res: CreateParticipantCmdSuccess => Protocol.CreateParticipantCmdSuccess(res.id)
        case CreateParticipantCmdFaild(id, error) => Protocol.CreateParticipantCmdFailed(id, error)
      }

  override def get: Flow[Protocol.GetParticipantCmdReq, Protocol.GetParticipantCmdRes, NotUsed] =
    Flow[Protocol.GetParticipantCmdReq]
      .map(req => GetParticipantCmdReq(id = req.id))
      .mapAsync(1)(aggregateRef ? _)
      .map {
        case res: GetParticipantCmdSuccess => Protocol.GetParticipantCmdSuccess(res.id)
        case GetParticipantCmdFaile(id, error) => Protocol.GetParticipantCmdFailed(id, error)
      }


  override def assignTask: Flow[Protocol.AssignTaskCmdReq, Protocol.AssignTaskCmdRes, NotUsed] =
    Flow[Protocol.AssignTaskCmdReq]
      .map(req => AssignTaskCmdReq(
        id = req.id,
        pInstId = req.pInstId,
        description = req.description,
        activity = req.activity,
        actions = req.actions
      ))
      .mapAsync(1)(aggregateRef ? _)
      .map {
        case res: GetParticipantCmdSuccess => Protocol.AssignTaskCmdSuccess()
        case GetParticipantCmdFaile(id, error) => Protocol.AssignTaskCmdFailed(id, error)
      }

}

