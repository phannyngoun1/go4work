package com.dream.workflow.model

import java.util.UUID

import com.dream.common.Model.ResponseJson
import play.api.libs.json.{Format, Json}

object WorkflowModel {

  case class CreateItemJson  (
    id: UUID,
    errorMessages: Seq[String] = Seq.empty
  ) extends ResponseJson {
    override val isSuccessful: Boolean = errorMessages.isEmpty
  }

  object CreateItemJson {
    implicit val format: Format[CreateItemJson] = Json.format
  }

}
