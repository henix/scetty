package henix.scetty

import java.net.URI
import java.nio.charset.{StandardCharsets, Charset}

import org.eclipse.jetty.client.api.ContentProvider
import org.eclipse.jetty.client.util.{StringContentProvider, FormContentProvider}
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.util.{Fields, UrlEncoded, MultiMap}

case class HttpReq(
  method: HttpMethod,
  url: URI,
  headers: Traversable[(String, String)],
  body: Option[HttpBody]
)

sealed trait HttpBody {
  def toContent: ContentProvider
}

case class FormBody(params: Traversable[(String, String)] = List.empty, charset: Charset = StandardCharsets.UTF_8) extends HttpBody {
  override def toContent = {
    val fields = new Fields()
    for ((name, value) <- params) {
      fields.add(name, value)
    }
    new FormContentProvider(fields, charset)
  }
}

case class StringBody(data: String, charset: Charset = StandardCharsets.UTF_8) extends HttpBody {
  override def toContent = new StringContentProvider(data, charset)
}

object Url {

  def query(params: Traversable[(String, String)], charset: Charset = StandardCharsets.UTF_8): String = {
    // UrlEncoded.encode does not preserve parameters' order, but there are situations we need it
    params.map(kv => UrlEncoded.encodeString(kv._1, charset) + "=" + UrlEncoded.encodeString(kv._2, charset)).mkString("&")
  }

  def apply(baseUrl: String, params: Traversable[(String, String)], charset: Charset = StandardCharsets.UTF_8): String = {
    require(!baseUrl.contains("?"))
    if (params.isEmpty)
      baseUrl
    else
      baseUrl + "?" + query(params, charset)
  }
}
