package my.volkovandr

import akka.{Done, NotUsed}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import com.typesafe.config.ConfigFactory
import my.volkovandr.FutureClient.conf
import my.volkovandr.helpers.{AkkaImplicits, ClientMetrics}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object StreamClient extends App with ClientMetrics with AkkaImplicits {
  val conf = ConfigFactory.load()
  val host = conf.getString("client.host")
  val port = conf.getInt("client.port")
  val uri = conf.getString("client.uri")

  val processes = conf.getInt("client.threads")
  val requests = conf.getInt("client.requests")

  val requestSource: Source[HttpRequest, NotUsed] = Source(1 to requests).map(_ => HttpRequest(uri = uri)).async
  val connectionFlows: Seq[Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]] = for(i <- 1 to processes) yield Http().outgoingConnection(host, port).async
  val receiverSink: Sink[HttpResponse, Future[Done]] = Sink.foreach(resp => {/*println(resp);*/ counter.inc() })

  val runnable = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val balance = builder.add(Balance[HttpRequest](processes))
    val merge = builder.add(Merge[HttpResponse](processes))

    requestSource ~> balance
    connectionFlows.foreach(flow => balance ~> flow ~> merge)
    merge ~> receiverSink

    ClosedShape
  })

  runnable.run()

  println("started!")
}
