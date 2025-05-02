package com.socrata.imageresize

import com.rojoma.json.v3.interpolation._

import com.socrata.http.server.implicits.httpResponseToChainedResponse
import com.socrata.http.server.responses.{OK, BadRequest, Json}
import com.socrata.http.server.routing.SimpleResource

class DimensionsService extends SimpleResource {
  override def post = { req =>
    ImageUtilities.getImageSize(req.inputStream) match {
      case Some(ImageUtilities.Dimensions(x, y)) => OK ~> Json(json"""{x: $x, y: $y}""")
      case None => BadRequest ~> Json(
          json"""{message: "Bad request, unable load image."}""")
    }
  }
}
