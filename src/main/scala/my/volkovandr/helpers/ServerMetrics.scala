package my.volkovandr.helpers

import com.typesafe.config.ConfigFactory
import io.prometheus.client
import io.prometheus.client.Counter

trait ServerMetrics extends Metrics {
  private val conf = ConfigFactory.load()
  initMetrics(conf.getInt("server.metrics-port"))

  val counter: client.Counter = Counter.build()
    .name("sent_responses_total")
    .help("Total number of HTTP responses processed and sent by the server")
    .register()

}
