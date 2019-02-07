package com.dream.workflow.usecase

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.{Params, Participant, StartAction, StartActivity, Flow => WFlow}
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.{CreatePInstCmdRequest => createInst}
import com.dream.workflow.usecase.ItemAggregateUseCase.Protocol.{GetItemCmdRequest, GetItemCmdSuccess}
import com.dream.workflow.usecase.WorkflowAggregateUseCase.Protocol.{GetWorkflowCmdRequest, GetWorkflowCmdSuccess}
import com.dream.workflow.usecase.port.{ItemAggregateFlows, ProcessInstanceAggregateFlows, WorkflowAggregateFlows}

import scala.concurrent.{ExecutionContext, Future, Promise}


object ProcessInstanceAggregateUseCase {

  object Protocol {

    sealed trait ProcessInstanceCmdResponse

    sealed trait ProcessInstanceCmdRequest

    sealed trait CreateInstanceCmdResponse extends ProcessInstanceCmdResponse

    case class CreatePInstCmdRequest(
      itemID: UUID,
      by: Participant,
      params: Option[Params] = None
    ) extends ProcessInstanceCmdRequest

    sealed trait CreatePInstCmdResponse

    case class CreatePInstCmdSuccess(
      folio: String
    ) extends CreatePInstCmdResponse

    case class CreatePInstCmdFailed(
      message: String
    ) extends CreatePInstCmdResponse

  }

}

class ProcessInstanceAggregateUseCase(
  processInstanceAggregateFlows: ProcessInstanceAggregateFlows,
  workflowAggregateFlows: WorkflowAggregateFlows,
  itemAggregateFlows: ItemAggregateFlows
)(implicit system: ActorSystem)
  extends UseCaseSupport {

  import ProcessInstanceAggregateUseCase.Protocol._
  import UseCaseSupport._

  implicit val mat: Materializer = ActorMaterializer()


  private val prepareCreateInst = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._


    val broadcast = b.add(Broadcast[CreatePInstCmdRequest](2))
    var createInstZip = b.add(Zip[WFlow, CreatePInstCmdRequest])

    val flow1 = Flow[CreatePInstCmdRequest].map(r => GetItemCmdRequest(r.itemID))

    broadcast.out(0) ~> flow1 ~> itemAggregateFlows.getItem.map {
      case res: GetItemCmdSuccess => GetWorkflowCmdRequest(res.workflowId)
    } ~> workflowAggregateFlows.getWorkflow.map {
      case GetWorkflowCmdSuccess(workflow) => workflow
    } ~> createInstZip.in0
    broadcast.out(1) ~> createInstZip.in1

    val out = createInstZip.out.map(f => {

      val flow = f._1
      val req = f._2
      val startAction = StartAction()
      val startActivity = StartActivity()
      val nextFlow = flow.nextActivity(startAction, startActivity, req.by, false) match {
        case Right(flow) => flow
      }

      createInst(
        UUID.randomUUID(),
        flow.id,
        "test",
        "ticket",
        StartActivity(),
        StartAction(),
        req.by,
        "Test",
        nextFlow.participants,
        nextFlow.activity,
        "todo"
      )
    }).via(processInstanceAggregateFlows.createInst)

    FlowShape(broadcast.in, out.outlet)
  })


  private val createInstanceFlow
  : SourceQueueWithComplete[(CreatePInstCmdRequest, Promise[CreatePInstCmdResponse])] = Source
    .queue[(CreatePInstCmdRequest, Promise[CreatePInstCmdResponse])](10, OverflowStrategy.dropNew)
    .via(prepareCreateInst.zipPromise)
    .toMat(completePromiseSink)(Keep.left)
    .run()

  def createItem(request: CreatePInstCmdRequest)(implicit ec: ExecutionContext): Future[CreatePInstCmdResponse] = {
    offerToQueue(createInstanceFlow)(request, Promise())
  }


}
