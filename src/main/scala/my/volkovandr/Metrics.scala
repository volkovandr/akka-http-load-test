package my.volkovandr

import io.prometheus.client.exporter.HTTPServer

trait Metrics {

    private var prometheusHttpServer: HTTPServer = _

    def initMetrics(metricsPort: Int): Unit = {
      prometheusHttpServer = new HTTPServer(metricsPort, true)
    }

    def shutdownMetrics(): Unit =
      if(prometheusHttpServer != null) {
        prometheusHttpServer.stop()
        prometheusHttpServer = null
      }

}
