package my.volkovandr

import com.typesafe.config.ConfigFactory
import io.prometheus.client.Counter
import my.volkovandr.FutureClient.conf
import my.volkovandr.helpers.{ClientMetrics, Metrics}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import scalaj.http.{Http, HttpRequest}

object SyncClient extends App with ClientMetrics {
  import scala.concurrent.ExecutionContext.Implicits.global

  val conf = ConfigFactory.load()
  val url = conf.getString("client.url")
  val threads = conf.getInt("client.threads")
  val requests = conf.getInt("client.requests")

  val futures: Seq[Future[Unit]] = for(thread <- 1 to threads) yield {
    val f = Future {
      val request: HttpRequest = Http(url)
      for(cycle <- 1 to requests) {
        val resp = request.asString
        if(cycle % 10000 == 0) println(s"$cycle: $resp")
        counter.inc()
      }
    }
    f onComplete {
      case Success(_) => println("cool")
      case Failure(_) => println("fuck")
    }
    f
  }
  val futSeq = Future.sequence(futures)
  Await.result(futSeq, Duration.Inf)
  println("All done!")

}
