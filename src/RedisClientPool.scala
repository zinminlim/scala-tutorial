import com.redis._
import org.apache.commons._


object RedisClientPool extends App {

  val clients = new RedisClientPool("localhost", 6379)

  def set(msgs: List[String]) = {
    clients.withClient {
      client => {
        var i = 0
        msgs.foreach { v =>
          println("setting:"+ "key-%d".format(i) + "=>" + v)
          client.set("key-%d".format(i), v)
          i += 1
        }
        Some(10)
      }
    }
  }

  def setSingle(key: String , value: String) = {
    clients.withClient {
      client => {
        println("setting:"+ key + "=>" + value)
        client.set(key, value)
      }
    }
  }

  /*
  var names = List("dean", "Jas")
  set(names)
  */

  setSingle("deankey", "dean")

  println("done....")




}