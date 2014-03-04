import com.redis._


object RedisClientSimpleTest extends App {

  val r = new RedisClient("localhost", 6379)
  r.set("key1", "some value new")
  r.get("key1")

  println("done....")

}

