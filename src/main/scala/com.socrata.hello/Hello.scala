package com.socrata.hello

import javax.servlet.http.HttpServletRequest

import org.slf4j.LoggerFactory
import com.rojoma.json.v3.interpolation._

import com.socrata.http.server.SocrataServerJetty
import com.socrata.http.server.implicits._
import com.socrata.http.server.responses.{NotFound, Content, Json}
import com.socrata.http.server.util.RequestId.ReqIdHeader
import com.socrata.http.server.util.handlers.{LoggingOptions, NewLoggingHandler}
import com.socrata.http.server.routing.SimpleRouteContext.{Route, Routes}
import com.socrata.http.server.{HttpRequest, HttpResponse, HttpService}

class Router(helloService: => HttpService) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val logWrapper = NewLoggingHandler(LoggingOptions(logger, Set("X-Socrata-Host",
                                                                        "X-Socrata-Resource",
                                                                        ReqIdHeader))) _

  val routes = Routes(Route("/hello", helloService))

  /** 404 error. */
  val notFound: HttpService = { req =>
    logger.warn("path not found: {}", req.requestPathStr)
    NotFound ~> Json(json"""{error:"not found"}""")
  }

  val route: HttpRequest => HttpResponse = {
    req => logWrapper(routes(req.requestPath).getOrElse(notFound))(req)
  }
}

object Hello extends App {
  val router = new Router(new HelloService())

  val server = new SocrataServerJetty(
    handler = router.route,
    options = SocrataServerJetty.defaultOptions.
      withPort(8888).
      withPoolOptions(SocrataServerJetty.Pool.defaultOptions.withMinThreads(10)))

  server.run()
}
