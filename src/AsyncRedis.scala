import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import com.redis.RedisClientPool
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._



class AsyncRedis {

  // set up Executors
  val system = ActorSystem("ScatterGatherSystem")

  import system.dispatcher

  val timeout = 5 seconds

  def flow[A](key: String,
                            value: String,
                            redisClients : RedisClientPool,
                            fn: (String, String, RedisClientPool) => A) = {
    Future {
      fn(key, value, redisClients)
    }
  }


  def listPush(key : String, value : String, redisClients : RedisClientPool){
    redisClients.withClient {
      client => {
        println("setting:"+ key + "=>" + value)
        client.set(key, value)
      }
    }
  }


  def redisAsyncSet(key: String, value: String, redisClients : RedisClientPool) = {
    val futurePushes = flow(key, value, redisClients, listPush)
  }

}



object RedisAsyncMain extends App{

  var redisMain = new AsyncRedis;
  val clients = new RedisClientPool("localhost", 6379)

  var starValue = System.nanoTime();

  for( a <- 1 to 1000){
    redisMain.redisAsyncSet("dean","deanvalue", clients)
    redisMain.redisAsyncSet("dean1","deanvalue", clients)
    redisMain.redisAsyncSet("dean2","deanvalue", clients)
    redisMain.redisAsyncSet("dean3","deanvalue", clients)
    redisMain.redisAsyncSet("dean4","deanvalue", clients)
  }

  var endTime = (System.nanoTime() - starValue)/1000000


  println("EndTime: " + endTime + "ms");

  //Thread.sleep(1000)

}



/*
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
