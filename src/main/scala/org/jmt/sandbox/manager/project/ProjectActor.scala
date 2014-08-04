package org.jmt.sandbox.manager.project

import akka.actor.Actor
import java.util.UUID
import akka.event.Logging
import org.jmt.sandbox.Model.Project
import org.jmt.sandbox.Commands.{CreateProject,ProjectCommandResult}
import org.jmt.sandbox.Queries.Get


class ProjectActor(val clientId: String, val id: UUID) extends Actor {
  val log = Logging(context.system, this)
  
  var state: Option[Project] = None
  
  def receive = notStarted

  val notStarted: Receive = {
    case CreateProject(name) => {
      log.info(s"Creating new project for client $clientId, projectId=$id")
      state = Some(Project(name, "new", List()))
      sender ! ProjectCommandResult(clientId, id, None)
      context become started
    }
    case _ => sender ! ProjectCommandResult(clientId, id, Some("(BAD REQUEST) project not yet started"))
  }
  
  val started: Receive = {
    case _ => sender ! ProjectCommandResult(clientId, id, Some("(BAD REQUEST) already exists"))
  }

  val closed: Receive = {
    case _ => sender ! ProjectCommandResult(clientId, id, Some("(BAD REQUEST) project is closed"))
  }
  
  val queries: Receive = {
    case Get => sender ! state
    case _ => sender ! None
  }

}