package my.volkovandr.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaImplicits {
  implicit val system: ActorSystem = ActorSystem("Main")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: akka.util.Timeout = 60.seconds
}
