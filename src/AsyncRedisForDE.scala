import akka.actor._
import akka.actor.Terminated
import akka.util.Timeout
import com.redis.RedisClientPool
import com.typesafe.config.ConfigFactory
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.routing.{RoundRobinRouter, RoutedActorRef, Router}


class AsyncRedisForDE extends Actor {

  var redisEntryInfo = new RedisInfo

  def receive = {

    case Entry(key,value,redisClients) => {
      redisEntryInfo.addEntry( Entry(key, value, redisClients))
      if (redisEntryInfo.isListFull) {
          listPush(redisEntryInfo, redisClients)
          redisEntryInfo.resetInfo
      }
    }

    case _ => println("that was unexpected")
  }

  def listPush(redisEntryInfo : RedisInfo, redisClients : RedisClientPool){
    redisClients.withClient {
      client => {
        println(redisEntryInfo.redisEvalString)
        println(redisEntryInfo.keyList.toList.toString())
        client.evalBulk(redisEntryInfo.redisEvalString, redisEntryInfo.keyList.toList, redisEntryInfo.argList.toList)
        //Thread.sleep(20)
      }
    }
  }

}


object AsyncRedisForDEMain extends App {

  var config = ConfigFactory.load()

  // SetUp Executor
  val system = ActorSystem("ReidsActorSystem", config)
  //println(system.settings.config.toString)

  // SetUp RedisPoolClient
  val clients = new RedisClientPool("localhost", 6379)

  // timer start
  val startValue = System.nanoTime();
 // var myActor = system.actorOf(Props[AsyncRedisForDE], name = "myActor")

  // Actual configuration is in application.conf
  val redisRouter = system.actorOf(
    Props[AsyncRedisForDE].withRouter(RoundRobinRouter(
      nrOfInstances = 10,
      supervisorStrategy =
        SupervisorStrategy.defaultStrategy)),
    "RedisConnectionRouter")

  var key = ""

  for (i <- 1 to 50) {
    key = "jasdean" + i
    redisRouter ! Entry(key, "deanvalue", clients)
  }

  // timer end
  var endTime = (System.nanoTime() - startValue)/1000000
  println("EndTime: " + endTime + "ms");

  // sleep for 5 seconds
  Thread.sleep(5000)

  println("============================================ Shutting Down ===============================================")
  system.shutdown()
}
