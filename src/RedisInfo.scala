import com.redis.RedisClientPool
import scala.collection.mutable.ListBuffer

/**
 * Created by zlim on 3/3/14.
 */

case class Entry (key: String, value: String, redisClients : RedisClientPool)

class RedisInfo {

    var entryList  = new ListBuffer[Entry]

    var redisEvalString = ""

    var keyList = ListBuffer[String]()

    var argList = ListBuffer[String]()

    var keyCount = 1

    var argCount = 1

    def addEntry (entry : Entry){
        entryList.+=(entry)

        redisEvalString += "redis.call('setex', KEYS["
        redisEvalString += keyCount
        keyCount += 1
        redisEvalString += "], ARGV["
        redisEvalString += argCount
        redisEvalString += "], ARGV["
        argCount += 1
        redisEvalString += argCount
        argCount += 1
        redisEvalString += "]);"

        keyList += entry.key

        argList += RedisInfo.expireTime

        argList += entry.value
    }


    def resetInfo {
      entryList = new ListBuffer[Entry]
      redisEvalString = ""
      keyList = ListBuffer[String]()
      argList = ListBuffer[String]()
      keyCount = 1;
      argCount = 1;
    }

    def isListFull : Boolean = {
        entryList.size >= RedisInfo.maxNum
    }

}

object RedisInfo{
  var maxNum = 5
  var expireTime = "172800"
}
