package henix.scetty

import henix.scetty.Scetty._
import org.eclipse.jetty.client.HttpClient
import org.scalatest.AsyncFunSuite

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ScettyClientTest extends AsyncFunSuite {

  implicit lazy val execctx = ExecutionContext.fromExecutor(null)

  val scettyClient = {
    val jettyClient = new HttpClient()
    jettyClient.setConnectTimeout(5 * 1000)
    jettyClient.setAddressResolutionTimeout(5 * 1000)
    jettyClient.setMaxConnectionsPerDestination(1000)
    jettyClient.start()
    new ScettyClient(jettyClient, 10.seconds)
  }

  test("basic get") {
    scettyClient.sendAsString(Get("http://example.com/")).map(s => assert(s.contains("Example Domain")))
  }
}
