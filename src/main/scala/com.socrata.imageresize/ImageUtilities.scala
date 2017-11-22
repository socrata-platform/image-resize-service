package com.socrata.imageresize

import java.io._
import javax.imageio.{ImageReader, ImageWriter, ImageIO}
import javax.activation.{MimeType, MimeTypeParseException}
import java.awt.image.BufferedImage
import com.rojoma.simplearm.v2._
import org.imgscalr.Scalr
import org.apache.commons.io.FilenameUtils

/**
 * Convert images around. Uses the mime types from Java IIO.
  *
 * MimeTypes Supported by Java ImageIO
 *
 * image/x-portable-anymap
 * image/x-png
 * image/tiff
 * image/x-portable-pixmap
 * image/vnd.wap.wbmp
 * image/jpeg2000
 * image/x-portable-bitmap
 * image/x-bmp
 * image/png
 * image/jpeg
 * image/jp2
 * image/x-windows-bmp
 * image/gif
 * image/bmp
 * image/x-portable-graymap
 */
object ImageUtilities {
  case class Dimensions(width: Int, height: Int)

  def getMimeType(filename: String): Option[String] = {
    FilenameUtils.getExtension(filename) match {
      case null | "" => None
      case ext => getMimeType(ImageIO.getImageReadersBySuffix(ext))
    }
  }

  def getMimeType(is: InputStream): Option[String] = {
    val iis = ImageIO.createImageInputStream(is)
    val readers = ImageIO.getImageReaders(iis)

    getMimeType(readers)
  }

  def getMimeType(readers: java.util.Iterator[ImageReader]): Option[String] = {
    if (readers.hasNext) {
      val reader = readers.next()
      val mimeTypes = reader.getOriginatingProvider.getMIMETypes

      Some(mimeTypes(0)) // get the first
    } else {
      None
    }
  }

  def isMimeTypeSupported(mimeType: String) = {
    ImageIO.getReaderMIMETypes.contains(mimeMunging(mimeType))
  }

  def mimeMunging(mimeType: String) = {
    try {
      val parsed = new MimeType(mimeType)
      (parsed.getPrimaryType, parsed.getSubType) match {
        case ("image", "pjpeg") => "image/jpeg"
        case other => parsed.getBaseType
      }
    } catch {
      case _: MimeTypeParseException =>
        mimeType
    }
  }

  /**
   * Measure the dimension of an image on disk. Return an Array(x,y)
   */
  def getImageSize(is: InputStream): Option[Dimensions] = {
    try {
      val iis = ImageIO.createImageInputStream(is)
      val originalImage = ImageIO.read(iis)

      if (originalImage == null) {
        return None
      }

      Some(Dimensions(originalImage.getWidth, originalImage.getHeight))
    } catch {
      case e: Exception =>
        throw new IOException("Unable to size image", e)
    }
  }

  case class NotAnImageException() extends IOException("Unable to interpret image")

  def resizeImage(in: BufferedImage,
                  newSize: Dimensions,
                  respectAspect: Boolean): BufferedImage =
    Scalr.resize(in, Scalr.Method.ULTRA_QUALITY,
                 if (respectAspect) Scalr.Mode.AUTOMATIC else Scalr.Mode.FIT_EXACT,
                 newSize.width, newSize.height)

  def resizeImage(is: InputStream,
                  newSize: Dimensions,
                  outputMimeType: String,
                  respectAspect: Boolean): (OutputStream => Unit) = {
    val originalImage = readImage(is)

    try {
      val scaled = resizeImage(originalImage, newSize, respectAspect)

      writeImage(scaled, outputMimeType)
    } catch {
      case e: Exception =>
        throw new IOException("Unable to resize image", e)
    }
  }

  def cropImage(in: BufferedImage,
                offset: Dimensions,
                newSize: Dimensions): BufferedImage =
    Scalr.crop(in, offset.width, offset.height, newSize.width, newSize.height)

  def cropImage(is: InputStream,
                offset: Dimensions,
                newSize: Dimensions,
                outputMimeType: String): (OutputStream => Unit) = {
    val originalImage = readImage(is)
    try {
      val cropped = cropImage(originalImage, offset, newSize)

      writeImage(cropped, outputMimeType)
    } catch {
      case e: Exception =>
        throw new IOException("Unable to crop image", e)
    }
  }

  def readImage(is: InputStream): BufferedImage = {
    val originalImage = ImageIO.read(is)

    if (originalImage == null) {
      throw NotAnImageException()
    } else {
      originalImage
    }
  }

  def writeImage(image: BufferedImage, outputMimeType: String): (OutputStream => Unit) = { os =>
    val writers: java.util.Iterator[ImageWriter] =
      ImageIO.getImageWritersByMIMEType(mimeMunging(outputMimeType))
    if (writers.hasNext) {
      val writer = writers.next()
      val ios = ImageIO.createImageOutputStream(os)
      writer.setOutput(ios)
      writer.write(image)
      ios.close()
    } else {
      throw new IOException("No writers available for mime type " + mimeMunging(outputMimeType))
    }
  }

  def toBufferedImage(is: InputStream): BufferedImage = {
    val originalImageStream = ImageIO.createImageInputStream(is)
    val image = ImageIO.read(originalImageStream)
    // if image != null, then originalInputStream has already been closed and it is an error
    // to do so again.
    if (image == null) {
      originalImageStream.close()
    }

    image
  }
}
