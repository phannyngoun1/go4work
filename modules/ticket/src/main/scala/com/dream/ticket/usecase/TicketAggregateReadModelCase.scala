package com.dream.ticket.usecase

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.{Done, NotUsed}
import com.dream.common.UseCaseSupport
import com.dream.ticket.adaptor.aggregate.{TicketCreated, TicketEvent}
import com.dream.ticket.usecase.port.TicketAggregateReadModelFlows

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Promise}


class TicketAggregateReadModelCase(ticketAggregateReadModelFlows: TicketAggregateReadModelFlows)(implicit val system: ActorSystem) extends UseCaseSupport {

  import UseCaseSupport._

  private val bufferSize: Int = 10

  private implicit val mat: ActorMaterializer = ActorMaterializer()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val projectionFlow: Flow[TicketEvent, Done, NotUsed] =
    Flow[TicketEvent].flatMapConcat {
      case event: TicketCreated =>
        Source.single((event.ticketId))
          .via(ticketAggregateReadModelFlows.createTicketFlow)
    }


  private val ticketEventHandlersQueqe: SourceQueueWithComplete[(TicketEvent, Promise[Done])] =
    Source
      .queue[(TicketEvent, Promise[Done])](bufferSize, OverflowStrategy.dropNew)
      .via(projectionFlow.zipPromise)
      .toMat(completePromiseSink)(Keep.left)
      .run()

  def execute(ticketEvent: TicketEvent)(implicit ec: ExecutionContext): Future[Done] =
    offerToQueue(ticketEventHandlersQueqe)(ticketEvent, Promise())

}
