import akka.actor.{Actor, Props}

class ThanksActor extends Actor with Monitoring {
  override def receive: Receive = {
    case e =>
      println("received message " + e)

      val ref = context.actorOf(Props(classOf[GreetingActor]), "greeting")
//      ref ! 0
      sender() ! "Thanks!"
  }
}
