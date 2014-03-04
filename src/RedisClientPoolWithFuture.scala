import akka.actor.{Props, ActorSystem, Actor}
import com.redis._
import scala.concurrent.{Await, Future}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

case class RedisHash (key: String, value: String)


class RedisActor extends Actor {
  def receive = {
    case RedisHash =>  {
      //setSingle("deankk", "dean")
      sender ! "donestation"
    }

    case "redis" =>  {
      //setSingle("deankk", "dean")
      sender ! "Donkey"
    }
    case _ => println("that was unexpected")
  }
}

object RedisActor {

  val clients = new RedisClientPool("localhost", 6379)

  def setSingle(key: String , value: String) = {
    clients.withClient {
      client => {
        println("setting:"+ key + "=>" + value)
        client.set(key, value)
      }
    }
  }

}


object RedisClientPoolWithFuture extends App {
  implicit val timeout = Timeout(5 seconds)
  val system = ActorSystem("RedisPushSystem")
  val myActor = system.actorOf(Props[RedisActor], name = "myActor")

  var redisHash = new RedisHash("dean", "dog")

  val future5: Future[String] = ask(myActor, "redis").mapTo[String]
  val result5 = Await.result(future5, 1 second)


  //val future: Future[String] = ask(myActor, redisHash).mapTo[String]
  //val result = Await.result(future, 1 second)

  println(result5)




  println("done....")

}