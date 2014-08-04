package org.jmt.sandbox.manager

import akka.actor.Actor
import akka.actor.ActorRef
import org.jmt.sandbox.{Commands,Queries}
import akka.actor.Props

class RootManager extends Actor {
  
  var clientManagers = Map[String, ActorRef]()
  
  def receive = {
    case envelope: Commands.Envelope[_] => {
      val cm = clientManagers.get(envelope.clientId).getOrElse {
        val newCm = context.actorOf(Props(new ClientManager(envelope.clientId)),s"client-${envelope.clientId}")
        clientManagers = clientManagers + (envelope.clientId -> newCm)
        newCm
      }
      
      cm forward envelope
    }
    case envelope: Queries.Envelope[_] => {
      val cm = clientManagers.get(envelope.clientId).getOrElse {
        val newCm = context.actorOf(Props(new ClientManager(envelope.clientId)),s"client-${envelope.clientId}")
        clientManagers = clientManagers + (envelope.clientId -> newCm)
        newCm
      }
      
      cm forward envelope
    }
  }

}