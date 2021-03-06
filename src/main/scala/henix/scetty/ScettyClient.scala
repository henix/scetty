package henix.scetty

import java.util.concurrent.TimeUnit

import henix.scetty.Scetty._
import org.eclipse.jetty.client.{HttpClient, HttpContentResponse}
import org.eclipse.jetty.client.api.{ContentResponse, Result}
import org.eclipse.jetty.client.util.BufferingResponseListener

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

/**
 * @param jettyClient
 * @param defaultTimeout
 * @param defaultMaxLength max length of response, default 2 MiB
 */
class ScettyClient(jettyClient: HttpClient, defaultTimeout: FiniteDuration, defaultMaxLength: Int = 2 * 1024 * 1024) {

  def send(req: HttpReq, followRedirects: Option[Boolean] = None): Future[ContentResponse] = {
    val request = jettyClient.newRequest(req.url.toASCIIString).method(req.method) // Encode URL as ASCII #2
    for ((name, value) <- req.headers) {
      request.header(name, value)
    }
    req.body.foreach { body =>
      request.content(body.toContent)
    }

    request.timeout(defaultTimeout.toMillis, TimeUnit.MILLISECONDS)

    followRedirects.foreach { followRedirects =>
      request.followRedirects(followRedirects)
    }

    val p = Promise[ContentResponse]()
    request.send(new BufferingResponseListener(defaultMaxLength) {
      override def onComplete(result: Result) {
        if (result.isSucceeded) {
          p.success(new HttpContentResponse(result.getResponse, getContent, getMediaType, getEncoding))
        } else {
          p.failure(result.getFailure)
        }
      }
    })
    p.future
  }

  def sendAsString(req: HttpReq, followRedirects: Option[Boolean] = None)(implicit exec: ExecutionContext): Future[String] = send(req, followRedirects).map(mustOk).map(_.getContentAsString)
}
