package my.volkovandr.helpers

import com.typesafe.config.ConfigFactory
import io.prometheus.client
import io.prometheus.client.Counter

trait ClientMetrics extends Metrics {
  private val conf = ConfigFactory.load()
  initMetrics(conf.getInt("client.metrics-port"))

  val counter: client.Counter = Counter.build()
    .name("received_responses_total")
    .help("Total number of HTTP responses reseived from the server")
    .register()

}
