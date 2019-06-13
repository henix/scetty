# Scetty: Scala async http client based on jetty-client

## Install

```
"info.henix" %% "scetty" % "0.3"
```

DO NOT USE: v0.2.2

## Examples

### imports

```scala
import henix.scetty.Scetty._
import henix.scetty.{FormBody, Url}
```

### Create a ScettyClient

```scala
import scala.concurrent.duration._

val jettyClient = ... // Create your jetty client object here.

val scettyClient = new ScettyClient(jettyClient, 15.seconds) // This object can be safely shared between multiple threads.

// Don't forget to call jettyClient.stop() when your app exits.
```

### Simple GET

```scala
scettyClient.send(Get("http://www.example.com/"))
```

### GET with URL parameters

```scala
val url = Url("http://www.example.com/", List("q" -> "1")) // http://www.example.com/?q=1

scettyClient.send(Get(url))
```

`Url` will encode url query parameters [in the right way](https://web.archive.org/web/20151229061347/http://blog.lunatech.com/2009/02/03/what-every-web-developer-must-know-about-url-encoding).

### GET with headers

```scala
scettyClient.send(Get(
  "http://www.example.com/",
  headers = List("X-Requested-With" -> "XMLHttpRequest")
))
```

### POST with form

And encoded in charset other than UTF-8

```scala
val req = Post(
  "http://www.example.com/",
  headers = List("Referer" -> "https://github.com/"),
  body = FormBody(
    params = List(
	  "q" -> "1"
    ),
    charset = Charset.forName("GBK")
  )
)

scettyclient.send(req)
```

### Read response as a String

`send` will return a `Future[ContentResponse]`. See jetty-client's document for what can you do with [ContentResponse](http://download.eclipse.org/jetty/stable-9/apidocs/org/eclipse/jetty/client/api/ContentResponse.html).

If you want to get a `Future[String]`:

```scala
def mustOk(resp: ContentResponse): ContentResponse = {
  val status = resp.getStatus
  if (status == 200)
    resp
  else
    throw new UpstreamHttpException(resp.getRequest.getURI.toString, status, resp.getHeaders.iterator().asScala.map(f => f.getName -> f.getValue).toList)
}

def sendAsString(req: HttpReq, followRedirects: Option[Boolean] = None): Future[String] = send(req, followRedirects).map(mustOk).map(_.getContentAsString)
```

### Cache all GET requests

Scetty doesn't have caching functionality because I think it's better to keep it focus on only one task.

You can write a simple wrapper if you want to cache. An example using Guava's `CacheBuilder`:

```scala
import com.google.common.cache.CacheBuilder
import henix.scetty.HttpReq

val cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build[HttpReq, ContentResponse]()

def send(req: HttpReq, followRedirects: Option[Boolean] = None): Future[ContentResponse] = {
  val cached = cache.getIfPresent(req)
  if (cached ne null) {
    logger.info("cache.hit: {}", req.url)
    Future.successful(cached)
  } else {
    val f = scettyClient.send(req, followRedirects)
    if (req.method == HttpMethod.GET) {
      f.onSuccess { case r => cache.put(req, r) }
    }
    f
  }
}
```

`HttpReq` is a case class, it can be used as a Map's key.

## Features

* Allow charset other than UTF-8 to be used in url / post form
* Async support with `scala.concurrent.Future`
* Represent HTTP request as a case class `HttpReq`

## Links

* [Java / Scala http client 库的选择](https://www.douban.com/note/446442212/)
