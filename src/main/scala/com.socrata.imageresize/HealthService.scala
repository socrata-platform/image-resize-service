package com.socrata.imageresize

import com.rojoma.json.v3.interpolation._

import com.socrata.http.server.implicits.httpResponseToChainedResponse
import com.socrata.http.server.responses.{OK, Json}
import com.socrata.http.server.routing.SimpleResource

class HealthService extends SimpleResource {
  override def get = {
    req => OK ~> Json(json"""{healthy: true}""")
  }
}
