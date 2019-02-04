package com.dream.ticket.usecase.port

import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Flow}

trait TicketAggregateReadModelFlows {

  def createTicketFlow: Flow[(Long), Done, NotUsed]

}
