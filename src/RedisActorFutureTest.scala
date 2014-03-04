import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClientPool
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._



class RedisActorFutureTest {

  // set up Executors
  val system = ActorSystem("ScatterGatherSystem")

  import system.dispatcher

  val timeout = 5 seconds

 // val timeout = Timeout(5 minutes)

  println("hello.. Dean.. ")


  private[this] def flow[A](noOfRecipients: Int,
                            opsPerClient: Int,
                            keyPrefix: String,
                            redisClients : RedisClientPool,
                            fn: (Int, String, RedisClientPool) => A) = {
    (1 to noOfRecipients) map {
      i =>
        Future {
          fn(opsPerClient, "list_" + i, redisClients)
        }
    }
  }
  def listPush(numOps : Int, strVal : String, redisClients : RedisClientPool){
    println("pushy..")
    redisClients.withClient {
      client => {
        println("setting:"+ numOps + "=>" + strVal)
        val key = "dean"+numOps
        client.set(key, strVal)
      }
    }

  }



  // scatter across clients and gather them to do a sum
  def scatterGatherWithList(opsPerClient: Int, redisClients : RedisClientPool) = {
    // scatter
    val futurePushes = flow(1, opsPerClient, "list_", redisClients, listPush)

    /*
    // concurrent combinator: Future.sequence
    val allPushes = Future.sequence(futurePushes)

    // sequential combinator: flatMap
    val allSum = allPushes flatMap {
      result =>
      // gather
        val futurePops = flow(100, opsPerClient, "list_", listPop)
        val allPops = Future.sequence(futurePops)
        allPops map {
          members => members.sum
        }
    }
    Await.result(allSum, timeout).asInstanceOf[Long]

    */
  }
  /*
  // scatter across clietns and gather the first future to complete
  def scatterGatherFirstWithList(opsPerClient: Int)(implicit clients: RedisClientPool) = {
    // scatter phase: push to 100 lists in parallel
    val futurePushes = flow(100, opsPerClient, "seq_", listPush)

    // wait for the first future to complete
    val firstPush = Future.firstCompletedOf(futurePushes)

    // do a sum on the list whose key we got from firstPush
    val firstSum = firstPush map {
      key =>
        listPop(opsPerClient, key)
    }
    Await.result(firstSum, timeout).asInstanceOf[Int]
  } */





}

object RedisMain extends App{
  var redisMain = new RedisActorFutureTest;
  val clients = new RedisClientPool("localhost", 6379)

  redisMain.scatterGatherWithList(2, clients)
  println("hello");
}
