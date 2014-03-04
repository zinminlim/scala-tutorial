import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class HelloWorld extends Actor {
  def receive = {
    case "hello" => {
      println("hello back at you")
      //Thread.sleep(1000)
    }
    case _ => println("huh?")
  }
}

object Main extends App {

  // an actor needs an ActorSystem
  val system = ActorSystem("HelloSystem")

  // create and start the actor
  val helloActor = system.actorOf(Props[HelloWorld], name = "helloactor")

  // send the actor two messages
  helloActor ! "hello"
  helloActor ! "buenos dias"

  // shut down the system
  system.shutdown

}


/**
 * Created by zlim on 3/1/14.
 */

/*
object HelloWorld {
  def main(args: Array[String]) {

    println("Hello, world! birach")

  }
}  */