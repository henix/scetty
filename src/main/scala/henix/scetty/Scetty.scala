package henix.scetty

import java.net.URI

import org.eclipse.jetty.http.HttpMethod

import scala.language.implicitConversions

object Scetty {

  implicit def stringToURI(str: String): URI = new URI(str)

  def Get(url: URI, headers: Traversable[(String, String)] = List.empty) = HttpReq(HttpMethod.GET, url, headers, None)
  def Head(url: URI, headers: Traversable[(String, String)] = List.empty) = HttpReq(HttpMethod.HEAD, url, headers, None)
  def Post(url: URI, headers: Traversable[(String, String)] = List.empty, body: HttpBody) = HttpReq(HttpMethod.POST, url, headers, Some(body))
  def Put(url: URI, headers: Traversable[(String, String)] = List.empty, body: HttpBody) = HttpReq(HttpMethod.PUT, url, headers, Some(body))
}
