package henix.scetty

import java.util.concurrent.TimeUnit

import org.eclipse.jetty.client.{HttpClient, HttpContentResponse}
import org.eclipse.jetty.client.api.{Result, ContentResponse}
import org.eclipse.jetty.client.util.BufferingResponseListener

import scala.concurrent.{Promise, Future}
import scala.concurrent.duration._

class ScettyClient(jettyClient: HttpClient, defaultTimeout: FiniteDuration) {

  def send(req: HttpReq, followRedirects: Option[Boolean] = None): Future[ContentResponse] = {
    val request = jettyClient.newRequest(req.url).method(req.method)
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
    request.send(new BufferingResponseListener() {
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
}
