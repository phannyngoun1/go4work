package controllers

import java.util.UUID

import akka.actor.ActorSystem
import com.dream.workflow.adaptor.aggregate.{ItemAggregateFlowsImpl, LocalEntityAggregates, WorkflowAggregateFlowsImpl}
import com.dream.workflow.domain._
import com.dream.workflow.model.WorkflowModel.{CreateItemJson, ItemJson}
import com.dream.workflow.usecase.{ItemAggregateUseCase, WorkflowAggregateUseCase}
import com.dream.workflow.usecase.ItemAggregateUseCase.Protocol._
import com.dream.workflow.usecase.WorkflowAggregateUseCase.Protocol.{CreateWorkflowCmdRequest, CreateWorkflowCmdSuccess, GetWorkflowCmdRequest}
import javax.inject.{Inject, Singleton}
import com.dream.workflow.domain.{Action => FAction}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

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
  val itemAggregateUseCase = new ItemAggregateUseCase(new ItemAggregateFlowsImpl(localEntityAggregates))
  val workflowAggregateUseCase = new WorkflowAggregateUseCase(new WorkflowAggregateFlowsImpl(localEntityAggregates))

  def index = Action.async { implicit request =>

    itemAggregateUseCase.createItem(CreateItemCmdRequest(UUID.randomUUID(), "Default Item 1", "Default Item 1", UUID.randomUUID())).map {
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

    val startActionFlow = ActionFlow(
      action = StartAction(),
      activity = "Ticketing"
    )

    val startActivityFlow = ActivityFlow(
      activity = StartActivity(),
      participants = List.empty,
      actionFlows = List(startActionFlow)
    )


    val editTicketActionFlow = ActionFlow(
      action = FAction("Edit"),
      activity = "StayStill"
    )

    val closeTicketActionFlow = ActionFlow(
      action = FAction("Close"),
      activity = "Done"
    )

    val assignTicketActionFlow = ActionFlow(
      action = FAction("Assign"),
      activity = "StayStill"
    )

    val addCommentActionFlow = ActionFlow(
      action = FAction("Comment"),
      activity = "StayStill"
    )

    val ticketActivityFlow = ActivityFlow(
      activity = Activity("Ticketing"),
      participants = List.empty,
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

    workflowAggregateUseCase.createWorkflow(CreateWorkflowCmdRequest(UUID.randomUUID(), "Start", workflowList)).map {
      case res: CreateWorkflowCmdSuccess =>  Ok(s"${res.id}")
      case _ => Ok("Failed")
    }
  }

  def getWorkflow(id: String) = Action.async { implicit request =>
    workflowAggregateUseCase.getWorkflow(GetWorkflowCmdRequest(UUID.fromString(id))).map {
      case res: CreateWorkflowCmdSuccess => Ok(s"${res.id}")
      case _ => Ok("Failed")
    }
  }

}
