package controllers

import java.util.UUID

import akka.actor.ActorSystem
import com.dream.ticket.adaptor.aggregate.TicketAggregateFlowsImpl
import com.dream.ticket.adaptor.dao.TicketAggregateReadModelFlowsImpl
import com.dream.ticket.usecase.{TicketAggregateReadModelCase, TicketAggregateUseCase}
import com.dream.workflow.adaptor.aggregate.{ItemAggregateFlowsImpl, LocalEntityAggregates}
import com.dream.workflow.model.WorkflowModel.CreateItemJson
import com.dream.workflow.usecase.ItemAggregateUseCase
import com.dream.workflow.usecase.ItemAggregateUseCase.Protocol.{CreateItemCmdRequest, CreateItemCmdSuccess}
import com.typesafe.config.{Config, ConfigFactory}
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
//class HomeController @Inject()(userDAO: UserDAO, cc: ControllerComponents)
class HomeController @Inject()(cc: ControllerComponents)
  (implicit ec: ExecutionContext)
  extends AbstractController(cc) {
  implicit val system: ActorSystem = ActorSystem("ticket-system")
  val rootConfig: Config = ConfigFactory.load()
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)

  val ticketAggregateReadModelUseCase = new TicketAggregateReadModelCase(new TicketAggregateReadModelFlowsImpl(dbConfig.profile, dbConfig.db))

  val localEntityAggregates = system.actorOf(LocalEntityAggregates.props, LocalEntityAggregates.name)

  val ticketAggregateUseCase = new TicketAggregateUseCase(new TicketAggregateFlowsImpl(localEntityAggregates))
  val itemAggregateUseCase = new ItemAggregateUseCase(new ItemAggregateFlowsImpl(localEntityAggregates))

    def index = Action.async { implicit request =>

      itemAggregateUseCase.createItem(CreateItemCmdRequest(UUID.randomUUID(), "Default Item", "Default Item", UUID.randomUUID())).map {
        case res: CreateItemCmdSuccess => Ok(Json.toJson(CreateItemJson(res.id)))
        case _ => Ok("Failed")
      }
    }

}
