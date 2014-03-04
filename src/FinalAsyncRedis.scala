import akka.actor.{Props, Actor, ActorSystem}
import akka.util.Timeout
import com.redis.RedisClientPool
import com.typesafe.config.ConfigFactory
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


case class REntry (key: String, value: String, redisClients : RedisClientPool)

class FinalAsyncRedis extends Actor {

  def receive = {
    case REntry(key,value,redisClients) => {
        listPush(key, value, redisClients)
    }
    case _ => println("that was unexpected")
  }

  def listPush(key : String, value : String, redisClients : RedisClientPool){
    redisClients.withClient {
      client => {
        val t = Thread.currentThread()
       // System.out.println("name=" + name);
        println("setting:"+ key + "=>" + value + ": Thread" + t.getName )
        client.set(key, value)
        Thread.sleep(20)
      }
    }
  }

}


object FinalRedisAsyncMain extends App{

  var config = ConfigFactory.load()
  //config.entrySet().add()

  // SetUp Executor
  val system = ActorSystem("ReidsActorSystem", config)
  println(system.settings.config.toString)

  // SetUp RedisPoolClient
  val clients = new RedisClientPool("localhost", 6379)

  // timer start
  val startValue = System.nanoTime();
  var myActor = system.actorOf(Props[FinalAsyncRedis], name = "myActor")

  var key = ""

  for (i <- 1 to 500) {
   // var actorName = "myActor"+i;
   // myActor = system.actorOf(Props[FinalAsyncRedis], name = actorName)
    key = "jasdean" + i
    myActor ! REntry(key, "deanvalue", clients)
  }

  // timer end
  var endTime = (System.nanoTime() - startValue)/1000000
  println("EndTime: " + endTime + "ms");

  // sleep for 6 seconds
  Thread.sleep(6000)

  println("============================================ Shutting Down ===============================================")
  system.shutdown()
}



/*

  // implicit val timeout = Timeout(5 seconds)
  // val result = Await.result(future, timeout.duration).asInstanceOf[String]
  // println(result)


class AsyncRedis {

  // set up Executors
  val system = ActorSystem("ScatterGatherSystem")
  import system.dispatcher

  val timeout = 5 seconds

  private[this] def flow[A](noOfRecipients: Int,
                            opsPerClient: Int,
                            keyPrefix: String,
                            redisClients : RedisClientPool,
                            fn: (Int, String, RedisClientPool) => A) = {

        Future {
          fn(opsPerClient, "list_", redisClients)
        }
  }


  def listPush(numOps : Int, strVal : String, redisClients : RedisClientPool){
    redisClients.withClient {
      client => {
        val key = "dean"+numOps
        println("setting:"+ key + "=>" + strVal)
        client.set(key, strVal)
      }
    }
  }


  def redisAsyncSet(opsPerClient: Int, redisClients : RedisClientPool) = {
    val futurePushes = flow(1, opsPerClient, "list_", redisClients, listPush)
  }

}

object RedisAsyncMain extends App{
  var redisMain = new AsyncRedis;
  val clients = new RedisClientPool("localhost", 6379)

  redisMain.redisAsyncSet(2, clients)
  println("hello");
} */
