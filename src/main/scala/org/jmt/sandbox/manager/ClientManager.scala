package org.jmt.sandbox.manager

import akka.actor.Actor
import akka.actor.ActorRef
import org.jmt.sandbox.{Commands,Queries}
import java.util.UUID
import akka.actor.Props
import org.jmt.sandbox.manager.project.ProjectActor
import scala.concurrent.duration._
import akka.util.Timeout

class ClientManager(clientId: String) extends Actor {
  
  var projects = Map[UUID, ActorRef]()
  
  def receive = {
    case envelope: Commands.Envelope[_] => {
      val project = projects.get(envelope.projectId).getOrElse {
        val newProject = context.actorOf(Props(new ProjectActor(envelope.clientId, envelope.projectId )),s"project-${envelope.projectId}")
        projects = projects + (envelope.projectId -> newProject)
        newProject
      } 
      
      project forward envelope.command
    }
    
    case envelope: Queries.Envelope[_] => {
      val queryActor = context.actorFor(s"project-${envelope.projectId}")
      queryActor forward envelope.query
    }
  }

}

