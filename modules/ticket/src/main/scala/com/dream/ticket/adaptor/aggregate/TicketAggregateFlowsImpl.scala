package com.dream.ticket.adaptor.aggregate

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import scala.concurrent.duration._
import com.dream.ticket.usecase.TicketAggregateUseCase.Protocol
import com.dream.ticket.usecase.port.TicketAggregateFlows

class TicketAggregateFlowsImpl(aggregateRef: ActorRef) extends TicketAggregateFlows {

  import Protocol._

  private implicit val to: Timeout = Timeout(2.seconds)


  def takeAction =  {

    //Find workflow

    //Get activity flow

      // --validate participant
      // --trigger task in the activity
      // --update ticket persistent
  }



  override def createTicketFlow: Flow[CreateTicketRequest, TicketResponse, NotUsed] =
    Flow[CreateTicketRequest]

      //check for flow
      //
      .mapAsync(1)(aggregateRef ? _ )
      .map {
        case _ => TicketCreatedSuccess(1)
      }


}
