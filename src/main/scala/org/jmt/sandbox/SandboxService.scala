package org.jmt.sandbox

import akka.actor.Actor
import spray.routing.HttpService
import spray.routing.directives.LogEntry
import spray.http.HttpResponse
import akka.event.Logging
import spray.http.HttpRequest
import akka.actor.ActorRef
import akka.pattern._
import java.util.UUID
import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.StatusCodes
import spray.http.StatusCode
import spray.http.HttpHeaders.Location

class SandboxServiceActor(val rootManager: ActorRef) extends Actor with SandboxService {
  
  def actorSystem = context.system
  
  def actorRefFactory = context

  def receive = runRoute(route)
}

trait SandboxService extends HttpService { self: Actor => 
  
  val rootManager: ActorRef
  implicit val ec = context.system.dispatcher
  
  val route = logRequestResponse(requestMethodAndResponseStatusAsInfo _) {
    pathPrefix("sandbox" / "clients" / Segment) { clientId =>
      pathPrefix("projects") {
        path(JavaUUID) { projectId =>
          get {
            implicit val timeout = Timeout(5.seconds)
            val fres = ask(rootManager, Queries.Envelope(clientId,projectId,Queries.Get))
            complete(fres.map(_.toString)) 
          } 
        }~
        get {
          complete(s"Getting all projects for $clientId")
        } ~
        post {
          implicit val timeout = Timeout(5.seconds)

          entity(as[String]) { name => ctx =>
            val newProjectId = UUID.randomUUID
            val fres = ask(rootManager,Commands.Envelope(clientId, newProjectId,Commands.CreateProject(name))).mapTo[Commands.ProjectCommandResult]
            
            val locationHeader = {
              val projectPath = ctx.request.uri.path + newProjectId.toString
              Location(ctx.request.uri.copy(path = projectPath))
            }
            
            ctx.complete(fres.map(res => constructResponse(StatusCodes.Created,res.error).withHeaders(locationHeader)))
          }
        }
      }
    }
  }
 
  def constructResponse(code: StatusCode, error: Option[String]) = error.map( msg => HttpResponse(StatusCodes.InternalServerError,msg))
                                                                        .getOrElse(HttpResponse(code))
 
  def requestMethodAndResponseStatusAsInfo(req: HttpRequest): Any => Option[LogEntry] = {
    case res: HttpResponse => Some(LogEntry(s"${req.method} ${req.uri} -> ${res.message.status}", Logging.InfoLevel))
    case _ => None // other kind of responses
  }


}