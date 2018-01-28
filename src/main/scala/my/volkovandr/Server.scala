package my.volkovandr

import java.util.concurrent.Executors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object Server extends App {
  case class CountMessage(count: Int)
  implicit val counter2Json: RootJsonFormat[CountMessage] = jsonFormat1(CountMessage)

  val conf = ConfigFactory.load()
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  implicit val system: ActorSystem = ActorSystem("Main")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: akka.util.Timeout = 60.seconds

  val counter = system.actorOf(Props[Counter], "counter")

  val route = path("count") {
    get {
      //println("received")
      val f: Future[CountMessage] = (counter ? "inc").mapTo[CountMessage]
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
