package com.dream.workflow.usecase.port

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.dream.workflow.usecase.ProcessInstanceUseCase.Protocol.{GetFlowCmdRequest, GetFlowCmdResponse}

trait WorkflowAggregateFlows {
  def getWorkflow:  Flow[GetFlowCmdRequest, GetFlowCmdResponse, NotUsed]
  //Aggregate

}