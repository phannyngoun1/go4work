package com.dream.ticket.adaptor.dao

import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.dream.ticket.usecase.port.TicketAggregateReadModelFlows
import slick.jdbc.JdbcProfile

class TicketAggregateReadModelFlowsImpl(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database) extends TicketAggregateReadModelFlows {

  override def createTicketFlow: Flow[Long, Done, NotUsed] = Flow[Long].map(_ => Done)
}
