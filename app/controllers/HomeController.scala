package controllers

import java.util.UUID

import akka.actor.ActorSystem
import com.dream.workflow.adaptor.aggregate.{ItemAggregateFlowsImpl, LocalEntityAggregates, ProcessInstanceAggregateFlowsImpl, WorkflowAggregateFlowsImpl}
import com.dream.workflow.domain.{Action => FAction, _}
import com.dream.workflow.entity.processinstance.ProcessInstanceProtocol.GetPInstCmdRequest
import com.dream.workflow.model.WorkflowModel.{CreateItemJson, ItemJson}
import com.dream.workflow.usecase.ItemAggregateUseCase.Protocol._
import com.dream.workflow.usecase.ProcessInstanceAggregateUseCase.Protocol.{CreatePInstCmdRequest, CreatePInstCmdSuccess, GetPInstCmdSuccess}
import com.dream.workflow.usecase.ProcessInstanceAggregateUseCase.Protocol
import com.dream.workflow.usecase.WorkflowAggregateUseCase.Protocol._
import com.dream.workflow.usecase.{ItemAggregateUseCase, ProcessInstanceAggregateUseCase, WorkflowAggregateUseCase}
import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
//class HomeController @Inject()(userDAO: UserDAO, cc: ControllerComponents)
class HomeController @Inject()(cc: ControllerComponents)
  (implicit ec: ExecutionContext)
  extends AbstractController(cc) {
  implicit val system: ActorSystem = ActorSystem("ticket-system")
  //  val rootConfig: Config = ConfigFactory.load()
  //  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)
  //
  //  val ticketAggregateReadModelUseCase = new TicketAggregateReadModelCase(new TicketAggregateReadModelFlowsImpl(dbConfig.profile, dbConfig.db))

  val localEntityAggregates = system.actorOf(LocalEntityAggregates.props, LocalEntityAggregates.name)

  //  val ticketAggregateUseCase = new TicketAggregateUseCase(new TicketAggregateFlowsImpl(localEntityAggregates))
  val itemFlow = new ItemAggregateFlowsImpl(localEntityAggregates)
  val workFlow = new WorkflowAggregateFlowsImpl(localEntityAggregates)
  val pInstFlow = new ProcessInstanceAggregateFlowsImpl(localEntityAggregates)
  val itemAggregateUseCase = new ItemAggregateUseCase(itemFlow)
  val workflowAggregateUseCase = new WorkflowAggregateUseCase(workFlow)
  val processintance = new ProcessInstanceAggregateUseCase(pInstFlow, workFlow, itemFlow)

  def index = Action.async { implicit request =>

    Future.successful(Ok("hello"))
  }


  def createItem = Action.async { implicit request =>
    itemAggregateUseCase.createItem(CreateItemCmdRequest(UUID.randomUUID(), "Default ticket item", "Default ticket item", UUID.fromString("8806ef9d-8937-4725-9208-fea1646f1c45"))).map {
      case res: CreateItemCmdSuccess => Ok(Json.toJson(CreateItemJson(res.id)))
      case _ => Ok("Failed")
    }
  }

  def getItem(id: String) = Action.async { implicit request =>

    itemAggregateUseCase.getItem(GetItemCmdRequest(UUID.fromString(id))).map {
      case res: GetItemCmdSuccess => Ok(Json.toJson(ItemJson(res.id, res.name, res.desc, res.workflowId)))
      case _ => Ok("Failed")
    }
  }


  def createWorkflow = Action.async { implicit request =>

    val ticketActivity = Activity("Ticketing")

    val startActionFlow = ActionFlow(
      action = StartAction(),
      activity = ticketActivity
    )

    val startActivityFlow = ActivityFlow(
      activity = StartActivity(),
      participants = List.empty,
      actionFlows = List(startActionFlow)
    )


    val editTicketActionFlow = ActionFlow(
      action = FAction("Edit"),
      activity = CurrActivity()
    )

    val closeTicketActionFlow = ActionFlow(
      action = FAction("Close"),
      activity = DoneActivity()
    )

    val assignTicketActionFlow = ActionFlow(
      action = FAction("Assign"),
      activity = CurrActivity()
    )

    val addCommentActionFlow = ActionFlow(
      action = FAction("Comment"),
      activity = CurrActivity()
    )

    val ticketActivityFlow = ActivityFlow(
      activity = ticketActivity,
      participants = List(Participant(1), Participant(2)),
      actionFlows = List(
        editTicketActionFlow,
        closeTicketActionFlow,
        assignTicketActionFlow,
        addCommentActionFlow
      )
    )

    val workflowList: Seq[BaseActivityFlow] = Seq(
      startActivityFlow,
      ticketActivityFlow
    )

    workflowAggregateUseCase.createWorkflow(CreateWorkflowCmdRequest(UUID.randomUUID(), StartActivity(), workflowList)).map {
      case res: CreateWorkflowCmdSuccess => Ok(s"${res.id}")
      case _ => Ok("Failed")
    }
  }

  def getWorkflow(id: String) = Action.async { implicit request =>
    workflowAggregateUseCase.getWorkflow(GetWorkflowCmdRequest(UUID.fromString(id))).map {
      case GetWorkflowCmdSuccess(flow) => Ok(s"${flow.id  }")
      case GetWorkflowCmdFailed(id, error) => Ok(s"Failed, id: ${id}, Error: ${error.message} ")
    }
  }


  def createInstance = Action.async { implicit request =>

    val itemId = UUID.fromString("293c05d1-255e-4b4b-9f33-27951fcfaf19")
    processintance.createPInst(CreatePInstCmdRequest(
      itemId,
      Participant(1)

    )).map {
      case CreatePInstCmdSuccess(folio) =>  Ok(folio)
      case _ => Ok("Failed")
    }

  }

  def getInstance(id: String) = Action.async { implicit request =>
    processintance.getPInst(Protocol.GetPInstCmdRequest(UUID.fromString(id))).map {
      case cmd: GetPInstCmdSuccess => Ok(s"id: ${cmd.id}, folio: ${cmd.folio}")
      case _ => Ok("Failed")
    }

  }

}
