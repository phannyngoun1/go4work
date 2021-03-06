package com.dream.ticket.usecase

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.dream.common.UseCaseSupport
import com.dream.common.domain.Param
import com.dream.ticket.usecase.port.TicketAggregateFlows

import scala.concurrent.Future

object TicketAggregateUseCase {

  object Protocol {

    sealed trait TicketRequest {
      val ticketId: Long
    }

    sealed trait TicketResponse {
      val ticketId: Long
    }

    case class CreateTicketRequest(override val ticketId: Long, ticketItem: Int, by: Int, param: Param) extends TicketRequest

    case class TicketActionRequest(override val ticketId: Long, action: String, by: Int, param: Param) extends TicketRequest

    case class TicketActionTakenSuccess(override val ticketId: Long) extends TicketResponse

    case class TicketActionTakenFailed(override val ticketId: Long, message: String) extends TicketResponse

    case class TicketCreatedSuccess(override val ticketId: Long) extends TicketResponse

  }

}

class TicketAggregateUseCase(ticketAggregateFlows: TicketAggregateFlows)(implicit system: ActorSystem)
  extends UseCaseSupport {

  //  import TicketAggregateUseCase.Protocol._
  //  import UseCaseSupport._

  implicit val mat: Materializer = ActorMaterializer()

  //  def createTicket()(implicit ec: ExecutionContext): Future[TicketResponse] =
  //    offerToQueue(openBankAccountQueue)(CreateTicketRequest(1), Promise())
  //
  //  private val openBankAccountQueue
  //  : SourceQueueWithComplete[(CreateTicketRequest, Promise[TicketResponse])] = Source
  //    .queue[(CreateTicketRequest, Promise[TicketResponse])](10, OverflowStrategy.dropNew)
  //    .via(ticketAggregateFlows.createTicketFlow.zipPromise)
  //    .toMat(completePromiseSink)(Keep.left)
  //    .run()


  def initialModule(): Future[Done] = ???


}
