package com.socrata.imageresize

import java.io.IOException

import com.rojoma.json.v3.interpolation._
import org.slf4j.LoggerFactory

import com.socrata.http.server.implicits.httpResponseToChainedResponse
import com.socrata.http.server.responses.{OK, BadRequest, InternalServerError, ContentType, Json, Stream}
import com.socrata.http.server.routing.SimpleResource
import com.socrata.http.server.util.RequestId.ReqIdHeader
import com.socrata.http.server.util.handlers.{LoggingOptions, NewLoggingHandler}

class CropService extends SimpleResource {
  private val logger = LoggerFactory.getLogger(getClass)
  private val logWrapper = NewLoggingHandler(LoggingOptions(logger, Set("X-Socrata-Host",
                                                                        "X-Socrata-Resource",
                                                                        ReqIdHeader))) _

  override def post = { req =>
    req.parseQueryParametersAs[Int, Int, Int, Int, String, Boolean](
      "offsetX",
      "offsetY",
      "width",
      "height",
      "outputMimeType",
      "respectAspect") match {
      case Right((Some(offsetX),
                  Some(offsetY),
                  Some(width),
                  Some(height),
                  Some(outputMimeType),
                  maybeRespectAspect)) =>
        try {
          val respectAspect = maybeRespectAspect.getOrElse(false)
          val offset = ImageUtilities.Dimensions(offsetX, offsetY)
          val newSize = ImageUtilities.Dimensions(width, height)

          val writer = ImageUtilities.cropImage(req.inputStream, offset, newSize, outputMimeType)

          OK ~> ContentType(outputMimeType) ~> Stream(writer)
        } catch {
          case e: IOException =>
            logger.warn("Failed to crop image", e)
            InternalServerError ~> Json(json"""{error: "Failed to crop image"}""")
        }
      case Left(invalidParams) =>
        val errors = invalidParams.map { err => err.name -> err.rawValue }
        BadRequest ~> Json(
          json"""{message: "Bad request, unable to parse some parameters.", invalidParameters: ${errors}}""")
      case _ =>
        BadRequest ~> Json(
          json"""{message: "Bad request, 'offsetX', 'offsetY', 'width', 'height', and 'outputMimeType' are all required"}""")
    }
  }
}
