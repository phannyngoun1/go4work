package com.dream.workflow.entity.account

import java.util.UUID

import com.dream.common.Protocol.{CmdRequest, CmdResponse}

object AccountProtocol {

  sealed trait AccountCmdRequest extends CmdRequest {
    val id: UUID
  }

  sealed trait AccountCmdResponse extends CmdResponse


}
