package com.dream.workflow.adaptor.aggregate

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}

object WorkflowProtocol {

  sealed trait WorkFlowCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait WorkFlowCmdResponse extends CmdResponse {
    val id: UUID
  }

}
