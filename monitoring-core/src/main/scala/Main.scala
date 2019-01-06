import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Main extends App {
  val system = ActorSystem()
  val monitoringActor = MonitoringActor.ref()(system)
  val log = Logging(system, getClass)
  implicit val timeout = Timeout(60.seconds)
  implicit val ec: ExecutionContext = system.dispatcher

  val ref = system.actorOf(Props(classOf[GreetingActor]), "greeting")

  for {
    _ <- (ref ? "Hello").mapTo[String]
    s <- (monitoringActor ? MonitoringActor.GatherActors).mapTo[Map[String, Seq[ActorRef]]]
  } yield {
    log.warning("response: {}", s)
  }
}
