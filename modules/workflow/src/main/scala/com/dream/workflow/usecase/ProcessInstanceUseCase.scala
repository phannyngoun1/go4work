package com.dream.workflow.usecase

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.Workflow.{DoAction, Params}
import com.dream.workflow.domain.{Participant, Workflow}
import com.dream.workflow.usecase.ProcessInstanceUseCase.Protocol.WorkFlowActionCmdRequest
import com.dream.workflow.usecase.port.{ProcessInstanceAggregateFlows, WorkflowAggregateFlows}

import scala.concurrent.Promise


object ProcessInstanceUseCase {

  object Protocol {

    sealed trait ProcessInstanceCmdResponse
    sealed trait ProcessInstanceCmdRequest

    case class WorkFlowActionCmdRequest(
      doAction: DoAction
    ) extends ProcessInstanceCmdRequest

    case class CreateInstanceCmdRequest(
      itemID: Long,
      by: Participant,
      params: Option[Params] = None
    ) extends ProcessInstanceCmdRequest

    sealed trait CreateInstanceCmdResponse extends ProcessInstanceCmdResponse

    case class CreateInstanceSuccess(
      folio: String
    )extends CreateInstanceCmdResponse

    case class CreateInstanceFailed(
      message: String
    ) extends CreateInstanceCmdResponse

    case class GetFlowCmdRequest(
      id: UUID
    ) extends ProcessInstanceCmdRequest


    sealed trait GetFlowCmdResponse extends ProcessInstanceCmdResponse

    case class GetFlowSuccess(
      flow: Workflow
    ) extends GetFlowCmdResponse

    case class GetFlowFailed(
      message: String
    ) extends GetFlowCmdResponse
  }

}

class ProcessInstanceUseCase(processInstanceAggregateFlows: ProcessInstanceAggregateFlows, workflowAggregateFlows: WorkflowAggregateFlows )(implicit system: ActorSystem)
  extends UseCaseSupport {
  import UseCaseSupport._
  import ProcessInstanceUseCase.Protocol._

  implicit val mat: Materializer = ActorMaterializer()

  private val createInstance = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val in = Inlet[Int]("hello")
    var ot =Outlet[Int]("out")

    val f = Flow[Int].map(_ +1 )



    val bcast = b.add(Broadcast[CreateInstanceCmdRequest](1))
    val merge = b.add(Merge[Int](1))

    val convertToGerFlowRequest = Flow[CreateInstanceCmdRequest].map(cmd => GetFlowCmdRequest(cmd.) )

    //bcast.out(0)  ~>  ~> merge

    ot ~> f ~> in

    FlowShape(in, ot)
  })


  private val openBankAccountQueue
  : SourceQueueWithComplete[(GetFlowCmdRequest, Promise[GetFlowCmdResponse])] = Source
    .queue[(GetFlowCmdRequest, Promise[GetFlowCmdResponse])](10, OverflowStrategy.dropNew)
    .via(workflowAggregateFlows.getWorkflow.zipPromise)
    .toMat(completePromiseSink)(Keep.left)
    .run()



  def takeAction(request: WorkFlowActionCmdRequest) = {

  }

}
