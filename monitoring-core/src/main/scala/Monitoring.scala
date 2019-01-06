import akka.actor.{Actor, ActorLogging, ActorRef}

trait Monitoring extends Actor with ActorLogging {
  lazy val monitoringActor: ActorRef = MonitoringActor.ref()(context.system)

  override def preStart(): Unit = {
    monitoringActor ! MonitoringActor.PreStart(self)
    super.preStart()
  }

  override def postStop(): Unit = {
    monitoringActor ! MonitoringActor.PostStop(self)
    super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    monitoringActor ! MonitoringActor.PreRestart(self)
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    monitoringActor ! MonitoringActor.PostRestart(self)
    super.postRestart(reason)
  }
}
