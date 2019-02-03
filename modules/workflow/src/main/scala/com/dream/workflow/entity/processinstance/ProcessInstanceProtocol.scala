package com.dream.workflow.entity.processinstance

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}

object ProcessInstanceProtocol {

  sealed trait ProcessInstanceCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait ProcessInstanceCmdResponse extends CmdResponse {
    val id: UUID
  }



}
