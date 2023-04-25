package metric

import zio._
import zio.http._
import zio.http.model.Method
import zio.metrics.MetricKeyType.Counter
import zio.metrics._
import zio.metrics.connectors.prometheus.PrometheusPublisher
import zio.metrics.connectors.{MetricsConfig, prometheus}

object PrometheusPublisherApp {
  def apply(): ZIO[Any, Nothing, HttpApp[PrometheusPublisher, Nothing]] = ZIO.succeed {
    Http.collectZIO[Request] {
      case Method.GET -> !! / "metrics" =>
        ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
    }
  }

  def live: ULayer[PrometheusPublisher with MetricsConfig] = (metricsConfig ++ publisherLayer)

  def livePrometheusLayer: ZLayer[MetricsConfig & PrometheusPublisher, Nothing, Unit] = prometheusLayer

  private def metricsConfig = ZLayer.succeed(MetricsConfig(5.seconds))

  private def publisherLayer = prometheus.publisherLayer;

  private def prometheusLayer = prometheus.prometheusLayer

  // Example of creation custom metrics
  // personService.insertPerson(List(person)) @@ countAllRequests("", "")
  def countAllRequests(method: String, handler: String): Metric[Counter, Any, MetricState.Counter] =
    Metric.counterInt("count_all_requests").fromConst(1)
      .tagged(
        MetricLabel("method", method),
        MetricLabel("handler", handler)
      )
}