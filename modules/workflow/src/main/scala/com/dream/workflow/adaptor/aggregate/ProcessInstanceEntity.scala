package com.dream.workflow.adaptor.aggregate

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import com.dream.workflow.domain.ProcessInstance

object ProcessInstanceEntity {


  def prop = Props(new ProcessInstanceEntity)

  final val AggregateName  = "process_instance"

  def name(uuId: UUID): String = uuId.toString

}

class ProcessInstanceEntity extends PersistentActor with ActorLogging {

  import ProcessInstanceEntity._

  private var state: Option[ProcessInstance] = None

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String =  s"$AggregateName-${self.path.name}"
}
