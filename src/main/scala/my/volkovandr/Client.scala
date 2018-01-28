package my.volkovandr

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.prometheus.client.Counter

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Client extends App with Metrics {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val conf = ConfigFactory.load()
  val url = conf.getString("client.url")

  def sequentialRequest(i: Int): Unit = if(i > 0) {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    responseFuture
      .onComplete {
        case Failure(e) =>
          sys.error(s"something wrong: ${e.getMessage}")
          system.terminate()
        case Success(res) =>
          counter.inc()
          if(i % 100000 == 0) {
            println(s"$i: $res")
          }
          sequentialRequest(i - 1)
      }
  } else {
    println("done")
  }

  initMetrics(conf.getInt("client.metrics-port"))

  val counter = Counter.build()
    .name("received_responses_total")
    .help("Total number of HTTP responses reseived from the server")
    .register()

  sys.addShutdownHook(system.terminate())

  sequentialRequest(1000000)
  sequentialRequest(1000000)
  sequentialRequest(1000000)
  sequentialRequest(1000000)


}