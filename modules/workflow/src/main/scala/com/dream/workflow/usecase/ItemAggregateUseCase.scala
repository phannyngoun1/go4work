package com.dream.workflow.usecase

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.Item.ItemError
import com.dream.workflow.usecase.ItemAggregateUseCase.Protocol.{CreateItemCmdRequest, CreateItemCmdResponse}
import com.dream.workflow.usecase.port.ItemAggregateFlows

import scala.concurrent.{ExecutionContext, Future, Promise}

object ItemAggregateUseCase {

  object Protocol {
    sealed trait ItemCmdResponse
    sealed trait ItemCmdRequest

    case class CreateItemCmdRequest(
      id: UUID,
      name: String,
      desc: String,
      workflowId: UUID
    ) extends ItemCmdRequest

    abstract class  CreateItemCmdResponse extends ItemCmdResponse

    case class CreateItemCmdSuccess(id: UUID) extends CreateItemCmdResponse

    case class CreateItemCmdFailed(id: UUID, itemError: ItemError) extends CreateItemCmdResponse

    case class GetItemCmdRequest(
      id: UUID
    ) extends ItemCmdRequest

    abstract class GetItemCmdResponse extends ItemCmdResponse

    case class GetItemCmdSuccess(
      id: UUID,
      name: String,
      desc: String,
      workflowId: UUID
    ) extends GetItemCmdResponse

    case class GetItemCmdFailed(id: UUID, itemError: ItemError) extends GetItemCmdResponse
  }
}


class ItemAggregateUseCase(itemAggregateFlows: ItemAggregateFlows)(implicit system: ActorSystem) extends UseCaseSupport {

  import UseCaseSupport._
  import ProcessInstanceAggregateUseCase.Protocol._

  implicit val mat: Materializer = ActorMaterializer()

  private val bufferSize: Int = 10

  def createItem(request: CreateItemCmdRequest)(implicit ec: ExecutionContext): Future[CreateItemCmdResponse] = {
    offerToQueue(createItemQueue)(request, Promise())
  }


  private val createItemQueue: SourceQueueWithComplete[(CreateItemCmdRequest, Promise[CreateItemCmdResponse])] =
    Source.queue[(CreateItemCmdRequest, Promise[CreateItemCmdResponse])](bufferSize, OverflowStrategy.dropNew)
      .via(itemAggregateFlows.createItem.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

}
