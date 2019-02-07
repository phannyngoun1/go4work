package com.dream.workflow.adaptor.aggregate

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.CreatePInstCmdRequest
import com.dream.workflow.usecase.ProcessInstanceAggregateUseCase.Protocol
import com.dream.workflow.usecase.port.ProcessInstanceAggregateFlows

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ProcessInstanceAggregateFlowsImpl(aggregateRef: ActorRef) extends ProcessInstanceAggregateFlows {

  private implicit val to: Timeout = Timeout(2 seconds)

  override def createInst: Flow[CreatePInstCmdRequest, Protocol.CreatePInstCmdResponse, NotUsed] =
    Flow[CreatePInstCmdRequest].mapAsync(1) {
      case test =>
        println(test)
        Future.successful(Protocol.CreatePInstCmdSuccess("test"))
    }

  override def performTask: Flow[ProcessInstanceProtocol.PerformTaskCmdReq, ProcessInstanceProtocol.PerformTaskCmdRes, NotUsed] = ???
}
