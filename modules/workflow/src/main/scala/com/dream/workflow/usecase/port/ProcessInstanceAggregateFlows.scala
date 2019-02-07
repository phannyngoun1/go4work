package com.dream.workflow.usecase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.CreatePInstCmdRequest
import com.dream.workflow.usecase.ProcessInstanceAggregateUseCase.Protocol.CreatePInstCmdResponse

trait ProcessInstanceAggregateFlows {

  def createInst:  Flow[CreatePInstCmdRequest, CreatePInstCmdResponse, NotUsed]

}
