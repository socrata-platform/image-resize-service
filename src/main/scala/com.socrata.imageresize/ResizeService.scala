package com.socrata.imageresize

import java.io.IOException

import com.rojoma.json.v3.interpolation._
import org.slf4j.LoggerFactory

import com.socrata.http.server.implicits.httpResponseToChainedResponse
import com.socrata.http.server.responses.{OK, BadRequest, InternalServerError, ContentType, Json, Stream}
import com.socrata.http.server.routing.SimpleResource

class ResizeService extends SimpleResource {
  private val logger = LoggerFactory.getLogger(getClass)

  override def post = { req =>
    req.parseQueryParametersAs[Int, Int, String, Boolean](
      "width",
      "height",
      "outputMimeType",
      "respectAspect") match {
      case Right((Some(width), Some(height), Some(outputMimeType), maybeRespectAspect)) =>
        try {
          val respectAspect = maybeRespectAspect.getOrElse(false)
          val newSize = ImageUtilities.Dimensions(width, height)

          val writer = ImageUtilities.resizeImage(req.inputStream, newSize, outputMimeType, respectAspect)

          OK ~> ContentType(outputMimeType) ~> Stream(writer)
        } catch {
          case e: IOException =>
            logger.warn("Failed to resize image", e)
            InternalServerError ~> Json(json"""{error: "Failed to resize image"}""")
        }
      case Left(invalidParams) =>
        val errors = invalidParams.map { err => err.name -> err.rawValue }
        BadRequest ~> Json(
          json"""{message: "Bad request, unable to parse some parameters.", invalidParameters: ${errors}}""")
      case _ =>
        BadRequest ~> Json(
          json"""{message: "Bad request, 'width', 'height', and 'outputMimeType' are all required"}""")
    }
  }
}
