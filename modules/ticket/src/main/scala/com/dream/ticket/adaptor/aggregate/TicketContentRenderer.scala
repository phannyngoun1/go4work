package com.dream.ticket.adaptor.aggregate

import com.dream.ticket.domain.{DefaultContent, TicketContent}


object TicketContentRenderer {
  def apply(ticketContent: TicketContent): TicketContentRenderer = ticketContent match {
    case content: DefaultContent => new DefaultTicketContentRenderer(content)
  }
}


sealed trait TicketContentRenderer {
  def render(): String
}

class DefaultTicketContentRenderer(ticketContent: DefaultContent) extends TicketContentRenderer {
  override def render(): String = ???
}


