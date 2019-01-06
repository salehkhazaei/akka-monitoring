import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.cluster.MemberStatus
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object MonitoringActor {
  private var ref: Option[ActorRef] = None

  def ref()(implicit system: ActorSystem): ActorRef = {
    if (ref.isEmpty)
      ref = Some(system.actorOf(props, "monitoring-actor"))

    ref.get
  }

  def props = Props(classOf[MonitoringActor])

  case class PreStart(ref: ActorRef)
  case class PostStop(ref: ActorRef)
  case class PreRestart(ref: ActorRef)
  case class PostRestart(ref: ActorRef)

  case object FetchActors
  case class FetchActorsResponse(refs: Seq[ActorRef])

  case object FetchRestartingActors
  case class FetchRestartingActorsResponse(refs: Seq[ActorRef])


  case object GatherActors
  case class GatherActorsResponse(refs: Map[String, Seq[ActorRef]])

  case object GatherRestartingActors
  case class GatherRestartingActorsResponse(refs: Map[String, Seq[ActorRef]])

}

class MonitoringActor extends Actor with ActorLogging {
  implicit val ec: ExecutionContext = context.dispatcher

  var actors: Seq[ActorRef] = Seq.empty
  var restartingActors: Seq[ActorRef] = Seq.empty
  val cluster = akka.cluster.Cluster(context.system)


  override def receive: Receive = {
    case MonitoringActor.PreStart(ref) =>
      actors = actors :+ ref

    case MonitoringActor.PostStop(ref) =>
      actors = actors.filterNot(_ == ref)

    case MonitoringActor.PreRestart(ref) =>
      restartingActors = restartingActors :+ ref

    case MonitoringActor.PostRestart(ref) =>
      restartingActors = restartingActors.filterNot(_ == ref)

    case MonitoringActor.FetchActors =>
      sender() ! MonitoringActor.FetchActorsResponse(actors)

    case MonitoringActor.FetchRestartingActors =>
      sender() ! MonitoringActor.FetchRestartingActorsResponse(restartingActors)

    case MonitoringActor.GatherActors =>
      implicit val timeout: Timeout = Timeout(5.seconds)

      val future = cluster.state.members
        .filter(_.status == MemberStatus.Up)
        .map(_.uniqueAddress.address.toString + "/user/monitoring-actor")
        .map(address => (address, context.actorSelection(address)))
        .map(r => (r._2 ? MonitoringActor.FetchActors).mapTo[MonitoringActor.FetchActorsResponse].map(res => (r._1, res.refs)))

      (for {
        responses <- Future.sequence(future)
      } yield responses.toMap) pipeTo sender()

    case MonitoringActor.GatherRestartingActors =>
      implicit val timeout: Timeout = Timeout(5.seconds)

      val future = cluster.state.members
        .filter(_.status == MemberStatus.Up)
        .map(_.uniqueAddress.address.toString + "/user/monitoring-actor")
        .map(address => (address, context.actorSelection(address)))
        .map(r => (r._2 ? MonitoringActor.FetchRestartingActors).mapTo[MonitoringActor.FetchRestartingActorsResponse].map(res => (r._1, res.refs)))

      (for {
        responses <- Future.sequence(future)
      } yield responses.toMap) pipeTo sender()
  }
}
