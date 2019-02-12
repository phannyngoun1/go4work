package com.dream.workflow.usecase

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.dream.common.UseCaseSupport
import com.dream.workflow.domain.ProcessInstance.InstError
import com.dream.workflow.domain.{Params, ParticipantAccess, StartAction, StartActivity, Flow => WFlow}
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.{PerformTaskCmdReq, CreatePInstCmdRequest => createInst}
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
      by: UUID,
      params: Option[Params] = None
    ) extends ProcessInstanceCmdRequest

    sealed trait CreatePInstCmdResponse

    case class CreatePInstCmdSuccess(
      folio: String
    ) extends CreatePInstCmdResponse

    case class CreatePInstCmdFailed(
      id: UUID,
      error: InstError
    ) extends CreatePInstCmdResponse

    case class GetPInstCmdRequest(id: UUID) extends ProcessInstanceCmdRequest

    sealed trait  GetPInstCmdResponse extends ProcessInstanceCmdRequest
    case class GetPInstCmdSuccess(id: UUID, folio: String) extends GetPInstCmdResponse
    case class GetPInstCmdFailed(id: UUID, instError: InstError) extends GetPInstCmdResponse

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
    val createInstZip = b.add(Zip[WFlow, CreatePInstCmdRequest])
    val convertToGetItem = Flow[CreatePInstCmdRequest].map(r => GetItemCmdRequest(r.itemID))
    val convertToCreatePInstCmdReq = Flow[(WFlow, CreatePInstCmdRequest)].map(
      f => {
        val flow = f._1
        val req = f._2
        val startAction = StartAction()
        val startActivity = StartActivity()
        val nextFlow = flow.nextActivity(startAction, startActivity, ParticipantAccess(req.by), false) match {
          case Right(flow) => flow
        }
        createInst(
          UUID.randomUUID(),
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
      }
    )

    broadcast.out(0) ~> convertToGetItem ~> itemAggregateFlows.getItem.map {
      case res: GetItemCmdSuccess => GetWorkflowCmdRequest(res.workflowId)
    } ~> workflowAggregateFlows.getWorkflow.map {
      case GetWorkflowCmdSuccess(workflow) => workflow
    } ~> createInstZip.in0

    broadcast.out(1) ~> createInstZip.in1

    val createPrepareB = b.add(Broadcast[createInst](2))
    val convertToTaskCmdRequestFlow = Flow[createInst].map(p => PerformTaskCmdReq(p.id, p.activityId))

    val out = createInstZip.out ~> convertToCreatePInstCmdReq ~> createPrepareB ~> processInstanceAggregateFlows.createInst
    createPrepareB ~> convertToTaskCmdRequestFlow ~> processInstanceAggregateFlows.performTask ~> Sink.ignore

    FlowShape(broadcast.in, out.outlet)
  })


  private val createInstanceFlow
  : SourceQueueWithComplete[(CreatePInstCmdRequest, Promise[CreatePInstCmdResponse])] = Source
    .queue[(CreatePInstCmdRequest, Promise[CreatePInstCmdResponse])](10, OverflowStrategy.dropNew)
    .via(prepareCreateInst.zipPromise)
    .toMat(completePromiseSink)(Keep.left)
    .run()

  private val getPInstFlow: SourceQueueWithComplete[(GetPInstCmdRequest, Promise[GetPInstCmdResponse])] = Source
    .queue[(GetPInstCmdRequest, Promise[GetPInstCmdResponse])](10, OverflowStrategy.dropNew)
    .via(processInstanceAggregateFlows.getPInst.zipPromise)
    .toMat(completePromiseSink)(Keep.left)
    .run()

  def createPInst(request: CreatePInstCmdRequest)(implicit ec: ExecutionContext): Future[CreatePInstCmdResponse] = {
    offerToQueue(createInstanceFlow)(request, Promise())
  }

  def getPInst(request: GetPInstCmdRequest) (implicit ec: ExecutionContext): Future[GetPInstCmdResponse] =
    offerToQueue(getPInstFlow)(request, Promise())

}
