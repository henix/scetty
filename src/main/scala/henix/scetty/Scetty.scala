package henix.scetty

import java.net.URI

import org.eclipse.jetty.client.api.ContentResponse
import org.eclipse.jetty.http.HttpMethod

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.language.implicitConversions

object Scetty {

  implicit def stringToURI(str: String): URI = new URI(str)

  def Get(url: URI, headers: Traversable[(String, String)] = List.empty) = HttpReq(HttpMethod.GET, url, headers, None)
  def Head(url: URI, headers: Traversable[(String, String)] = List.empty) = HttpReq(HttpMethod.HEAD, url, headers, None)
  def Post(url: URI, headers: Traversable[(String, String)] = List.empty, body: HttpBody) = HttpReq(HttpMethod.POST, url, headers, Some(body))
  def Put(url: URI, headers: Traversable[(String, String)] = List.empty, body: HttpBody) = HttpReq(HttpMethod.PUT, url, headers, Some(body))

  def newHttpException(resp: ContentResponse) = new HttpException(resp.getRequest.getURI.toString, resp.getStatus, resp.getHeaders.iterator().asScala.map(f => f.getName -> f.getValue).toList)

  def mustOk(resp: ContentResponse): ContentResponse = {
    if (resp.getStatus == 200)
      resp
    else
      throw newHttpException(resp)
  }
}
