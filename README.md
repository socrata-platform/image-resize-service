# Image Resize Service #

A small HTTP service for resizing and cropping images.

## Quick run
`sbt image-resize-service/run`

##### 

To run the dockerized version

`sbt assembly`
`mv target/scala-2.11/image-resize-service-assembly-0.0.1.jar image-resize-service-assembly.jar`
`docker build .`
`docker run [machine_hash]`

## Starting the service ##

```sh
sbt image-resize-service/run
```

## Endpoints ##
* `/resize`:
  - `width` (required): The desired (max) width.
  - `height` (required): The desired (max) height.
  - `outputMimeType` (required): The desired mime type (see supported types).
  - `respectAspect`: Preserve aspect ratio -- treat width and height
    as maximums.
* `/crop`:
  - `offsetX` (required): How far from the left to start cropping.
  - `offsetY` (required): How far from the top to start cropping.
  - `width` (required): The desired width.
  - `height` (required): The desired height.
  - `outputMimeType` (required): The desired mime type (see supported types).

## Supported Mime Types ##
 * `image/x-portable-anymap`
 * `image/x-png`
 * `image/tiff`
 * `image/x-portable-pixmap`
 * `image/vnd.wap.wbmp`
 * `image/jpeg2000`
 * `image/x-portable-bitmap`
 * `image/x-bmp`
 * `image/png`
 * `image/jpeg`
 * `image/jp2`
 * `image/x-windows-bmp`
 * `image/gif`
 * `image/bmp`
 * `image/x-portable-graymap * image/x-portable-anymap`
 * `image/x-png`
 * `image/tiff`
 * `image/x-portable-pixmap`
 * `image/vnd.wap.wbmp`
 * `image/jpeg2000`
 * `image/x-portable-bitmap`
 * `image/x-bmp`
 * `image/png`
 * `image/jpeg`
 * `image/jp2`
 * `image/x-windows-bmp`
 * `image/gif`
 * `image/bmp`
 * `image/x-portable-graymap`
