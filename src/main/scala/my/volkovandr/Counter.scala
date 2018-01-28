package my.volkovandr

import akka.actor.Actor
import my.volkovandr.Server.CountMessage

class Counter extends Actor {
  var counter: Int = 0
  def receive(): PartialFunction[Any, Unit] = {
    case "inc" =>
      //Thread.sleep(3000)
      sender() ! CountMessage(counter)
      //println(s"sent $counter")
      counter += 1
  }
}
