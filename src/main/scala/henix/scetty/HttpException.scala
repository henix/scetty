package henix.scetty

class HttpException(url: String, status: Int, headers: List[(String, String)]) extends RuntimeException(s"url=$url | status=$status | headers=$headers")
