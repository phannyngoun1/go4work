package com.dream.workflow.usecase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol._
import com.dream.workflow.usecase.ProcessInstanceAggregateUseCase.Protocol.CreatePInstCmdResponse

trait ProcessInstanceAggregateFlows {

  def createInst:  Flow[CreatePInstCmdRequest, CreatePInstCmdResponse, NotUsed]
  def performTask: Flow[PerformTaskCmdReq, PerformTaskCmdRes, NotUsed]

}
