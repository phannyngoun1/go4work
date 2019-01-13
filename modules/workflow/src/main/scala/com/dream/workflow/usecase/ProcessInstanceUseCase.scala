package com.dream.workflow.usecase

import akka.actor.ActorSystem
import com.dream.common.UseCaseSupport
import com.dream.workflow.usecase.port.ProcessInstanceAggregateFlows


object ProcessInstanceUseCase {

  object Protocol {

    
  }

}

class ProcessInstanceUseCase(flow: ProcessInstanceAggregateFlows)(implicit system: ActorSystem)
  extends UseCaseSupport {
  import UseCaseSupport._



}
