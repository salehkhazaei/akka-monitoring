import akka.actor.{Actor, Props}

class GreetingActor extends Actor with Monitoring {

  override def receive: Receive = {
    case "Hello" =>
      println("received hi")
      val ref = context.actorOf(Props(classOf[ThanksActor]), "thanks")

      ref ! "hellooooooo"

      sender() ! "Hi!"

    case 0 =>
      val a = 10 / 0
  }
}
