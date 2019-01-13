package com.dream.ticket.adaptor.aggregate.tasks

import akka.actor.Props

trait Task {

  def props(): Props
  def taskName: String
  def taskCode: String

}
