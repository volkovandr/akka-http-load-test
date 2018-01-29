package my.volkovandr

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.typesafe.config.ConfigFactory
import my.volkovandr.helpers.{AkkaImplicits, ClientMetrics}

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

object FutureClient extends App with ClientMetrics with AkkaImplicits {

  val conf = ConfigFactory.load()
  val url = conf.getString("client.url")
  val threads = conf.getInt("client.threads")
  val requests = conf.getInt("client.requests")

  def requerst(): Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

  def sequentialRequest(i: Int): Unit = if(i > 0) {
    val responseFuture: Future[HttpResponse] = requerst()
    responseFuture
      .onComplete {
        case Failure(e) =>
          sys.error(s"something wrong: ${e.getMessage}")
          system.terminate()
        case Success(res) =>
          counter.inc()
          if(i % 10000 == 0) {
            println(s"$i: $res")
          }
          sequentialRequest(i - 1)
      }
  } else {
    println("done")
  }

  sys.addShutdownHook(system.terminate())

  (1 to threads).foreach(_ => sequentialRequest(requests))

}