package controllers

import akka.actor.ActorSystem
import com.dream.ticket.adaptor.aggregate.{TicketAggregateFlowsImpl, TicketAggregateMngt}
import com.dream.ticket.adaptor.dao.TicketAggregateReadModelFlowsImpl
import com.dream.ticket.usecase.TicketAggregateUseCase.Protocol.TicketCreatedSuccess
import com.dream.ticket.usecase.{TicketAggregateReadModelCase, TicketAggregateUseCase}
import com.example.user.UserDAO
import com.typesafe.config.{Config, ConfigFactory}
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
//class HomeController @Inject()(userDAO: UserDAO, cc: ControllerComponents)
class HomeController @Inject()( cc: ControllerComponents)
  (implicit ec: ExecutionContext)
  extends AbstractController(cc)
 {
  implicit val system: ActorSystem = ActorSystem("ticket-system")
  val rootConfig: Config = ConfigFactory.load()
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)

  val ticketAggregateReadModelUseCase = new TicketAggregateReadModelCase(new TicketAggregateReadModelFlowsImpl(dbConfig.profile, dbConfig.db))

  val ticketAggregateMngt = system.actorOf(TicketAggregateMngt.props(ticketAggregateReadModelUseCase), TicketAggregateMngt.name)

  val ticketAggregateUserCase = new TicketAggregateUseCase(new TicketAggregateFlowsImpl(ticketAggregateMngt))





  def index = Action.async { implicit request =>


    ticketAggregateUserCase.createTicket().map {

      case res: TicketCreatedSuccess => Ok(s"hello ${res.ticketId}")
      case _ => Ok("Failed")
    }
  }

}
