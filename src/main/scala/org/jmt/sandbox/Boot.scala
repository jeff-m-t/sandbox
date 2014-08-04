package org.jmt.sandbox

import akka.actor.ActorSystem
import akka.actor.Props
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import akka.io.IO
import spray.can.Http
import akka.pattern._
import org.jmt.sandbox.manager.RootManager

object Boot extends App {
  val config = ConfigFactory.load.getConfig("sandbox")
  
  implicit val system = ActorSystem("sandbox")
 
  val rootManager = system.actorOf(Props[RootManager],"manager")
  
  val service = system.actorOf(Props(new SandboxServiceActor(rootManager)), "sandbox-service")
  
  implicit val timeout = Timeout((Duration(config.getString("bind.timeout"))).toMillis, TimeUnit.MILLISECONDS)

  IO(Http) ? Http.Bind(service,
    interface = config.getString("bind.interface"),
    port = config.getInt("bind.port")
  )  
}