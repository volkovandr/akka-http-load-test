package my.volkovandr

import akka.actor.Actor
import io.prometheus.client
import my.volkovandr.Server.CountMessage

class CounterActor extends Actor {
  var counter: Int = 0
  var counterMetric: client.Counter = _
  def receive(): PartialFunction[Any, Unit] = {
    case "inc" =>
      sender() ! CountMessage(counter)
      counter += 1
      counterMetric.inc()
    case m: client.Counter => counterMetric = m
  }
}
