package my.volkovandr

import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import my.volkovandr.helpers.{AkkaImplicits, ServerMetrics}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.Future

object Server extends App with AkkaImplicits with ServerMetrics {
  case class CountMessage(count: Int)
  implicit val counter2Json: RootJsonFormat[CountMessage] = jsonFormat1(CountMessage)

  val conf = ConfigFactory.load()
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  val counterActor = system.actorOf(Props[CounterActor], "counter")
  counterActor ! counter

  val route = path("count") {
    get {
      val f: Future[CountMessage] = (counterActor ? "inc").mapTo[CountMessage]
      complete(f)
    }
  }

  val server = Http().bindAndHandle(route, host, port)

  sys.addShutdownHook(shutdown())

  def shutdown(): Unit = {
    server.flatMap(_.unbind()).onComplete(_ => {
      system.terminate()
      println("Therminated")
    })
  }

  println(s"The server is up and running on http://$host:$port")
}
