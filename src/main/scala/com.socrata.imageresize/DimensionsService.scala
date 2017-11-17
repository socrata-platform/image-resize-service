package com.socrata.imageresize

import java.io.IOException

import com.rojoma.json.v3.interpolation._
import org.slf4j.LoggerFactory

import com.socrata.http.server.implicits.httpResponseToChainedResponse
import com.socrata.http.server.responses.{OK, BadRequest, Json}
import com.socrata.http.server.routing.SimpleResource
import com.socrata.http.server.util.RequestId.ReqIdHeader

class DimensionsService extends SimpleResource {
  override def post = { req =>
    ImageUtilities.getImageSize(req.inputStream) match {
      case Some(ImageUtilities.Dimensions(x, y)) => OK ~> Json(json"""{x: $x, y: $y}""")
      case None => BadRequest ~> Json(
          json"""{message: "Bad request, unable load image."}""")
    }
  }
}
