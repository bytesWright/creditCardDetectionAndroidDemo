package com.isdavid.machine_vision

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBox
import java.io.ByteArrayOutputStream
import java.nio.ReadOnlyBufferException
import kotlin.experimental.inv
import kotlin.math.max
import kotlin.math.min


class ImageConversion {
    companion object {
        fun yuv420888ToNV21(image: Image): ByteArray {
            val planes = image.planes

            val width = image.width
            val height = image.height

            val bufferY = planes[0].buffer
            val bufferU = planes[1].buffer
            val bufferV = planes[2].buffer

            val yRowStride = image.planes[0].rowStride
            val uRowStride = image.planes[1].rowStride
            val vRowStride = image.planes[2].rowStride

            val uPixelStride = image.planes[1].pixelStride
            val vPixelStride = image.planes[2].pixelStride

            val nv21 = ByteArray(
                width * height *
                        ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
            )

            var yIndex = 0
            var uvIndex = width * height

            // Copy Y data directly
            val rowData = ByteArray(yRowStride)
            for (row in 0 until height) {
                bufferY.get(rowData, 0, yRowStride)
                System.arraycopy(rowData, 0, nv21, yIndex, width)
                yIndex += width
                bufferY.position(bufferY.position() + yRowStride - width)
            }

            // Interleave U and V data
            val rowUData = ByteArray(uRowStride)
            val rowVData = ByteArray(vRowStride)
            for (row in 0 until height / 2) {
                bufferU.get(rowUData, 0, uRowStride)
                bufferV.get(rowVData, 0, vRowStride)

                for (col in 0 until width / 2) {
                    nv21[uvIndex++] = rowVData[col * vPixelStride]
                    nv21[uvIndex++] = rowUData[col * uPixelStride]
                }

                bufferU.position(bufferU.position() + uRowStride - width / 2 * uPixelStride)
                bufferV.position(bufferV.position() + vRowStride - width / 2 * vPixelStride)
            }

            return nv21
        }

        fun YUV_420_888toNV21(image: Image): ByteArray {
            val width = image.width
            val height = image.height
            val ySize = width * height
            val uvSize = width * height / 4

            val nv21 = ByteArray(ySize + uvSize * 2)

            val yBuffer = image.planes[0].buffer // Y
            val uBuffer = image.planes[1].buffer // U
            val vBuffer = image.planes[2].buffer // V

            var rowStride = image.planes[0].rowStride
            assert(image.planes[0].pixelStride == 1)
            var pos = 0

            if (rowStride == width) { // likely
                yBuffer[nv21, 0, ySize]
                pos += ySize
            } else {
                var yBufferPos = -rowStride.toLong() // not an actual position
                while (pos < ySize) {
                    yBufferPos += rowStride.toLong()
                    yBuffer.position(yBufferPos.toInt())
                    yBuffer[nv21, pos, width]
                    pos += width
                }
            }

            rowStride = image.planes[2].rowStride
            val pixelStride = image.planes[2].pixelStride

            assert(rowStride == image.planes[1].rowStride)
            assert(pixelStride == image.planes[1].pixelStride)
            if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
                // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
                val savePixel = vBuffer[1]
                try {
                    vBuffer.put(1, savePixel.inv() as Byte)
                    if (uBuffer[0] == savePixel.inv() as Byte) {
                        vBuffer.put(1, savePixel)
                        vBuffer.position(0)
                        uBuffer.position(0)
                        vBuffer[nv21, ySize, 1]
                        uBuffer[nv21, ySize + 1, uBuffer.remaining()]

                        return nv21 // shortcut
                    }
                } catch (ex: ReadOnlyBufferException) {
                    // unfortunately, we cannot check if vBuffer and uBuffer overlap
                }

                // unfortunately, the check failed. We must save U and V pixel by pixel
                vBuffer.put(1, savePixel)
            }

            // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
            // but performance gain would be less significant
            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    val vuPos = col * pixelStride + row * rowStride
                    nv21[pos++] = vBuffer[vuPos]
                    nv21[pos++] = uBuffer[vuPos]
                }
            }

            return nv21
        }

        fun imageToMat(image: Image): ByteArray {
            val planes = image.planes

            val width = image.width
            val height = image.height

            val buffer0 = planes[0].buffer
            val buffer1 = planes[1].buffer
            val buffer2 = planes[2].buffer

            val bitesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)
            val bytesPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
            val totalBytesOutputBytes = image.width * image.height * bitesPerPixel / 8

            val data = ByteArray(totalBytesOutputBytes)

            val rowData1 = ByteArray(planes[1].rowStride)
            val rowData2 = ByteArray(planes[2].rowStride)


            // loop via rows of u/v channels
            var offsetY = 0

            val sizeY = width * height * bytesPerPixel
            val sizeUV = (width * height * bytesPerPixel) / 4

            for (row in 0 until height) {
                // fill data for Y channel, two row

                run {
                    val length = bytesPerPixel * width
                    buffer0[data, offsetY, length]

                    if (height - row != 1) buffer0.position(
                        buffer0.position() + planes[0].rowStride - length
                    )
                    offsetY += length
                }

                if (row >= height / 2) continue

                run {
                    var uvlength = planes[1].rowStride
                    if ((height / 2 - row) == 1) {
                        uvlength = width / 2 - planes[1].pixelStride + 1
                    }

                    buffer1[rowData1, 0, uvlength]
                    buffer2[rowData2, 0, uvlength]

                    // fill data for u/v channels
                    for (col in 0 until width / 2) {
                        // u channel
                        data[sizeY + (row * width) / 2 + col] =
                            rowData1[col * planes[1].pixelStride]

                        // v channel
                        data[sizeY + sizeUV + (row * width) / 2 + col] =
                            rowData2[col * planes[2].pixelStride]
                    }
                }
            }

            return data
        }


        fun nv21toJpegBytes(nv21: ByteArray, width: Int, height: Int): ByteArray {
            val out = ByteArrayOutputStream()
            val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)

            yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
            return out.toByteArray()
        }
    }
}

class BitmapOperations {
    companion object {
        fun rotate(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        fun crop(source: Bitmap, detectionBoundingBox: DetectionBoundingBox): Bitmap {
            val left = (source.width.toFloat() * detectionBoundingBox.x1).toInt()
            val right = (source.width.toFloat() * detectionBoundingBox.x2).toInt()

            val top = (source.height.toFloat() * detectionBoundingBox.y1).toInt()
            val bottom = (source.height.toFloat() * detectionBoundingBox.y2).toInt()

            val width = min(right, source.width) - left
            val height = min(bottom, source.height) - top

            return Bitmap.createBitmap(source, left, top, width, height)
        }

        fun crop(source: Bitmap, x1: Int, y1: Int, x2: Int, y2: Int): Bitmap {
            // Ensure the coordinates are in the correct order
            val left = min(x1, x2).coerceIn(0, source.width)
            val top = min(y1, y2).coerceIn(0, source.height)
            val right = max(x1, x2).coerceIn(0, source.width)
            val bottom = max(y1, y2).coerceIn(0, source.height)

            // Calculate the width and height of the cropped area
            val width = right - left
            val height = bottom - top

            // Create and return the cropped bitmap
            return Bitmap.createBitmap(source, left, top, width, height)
        }

        fun cropFromCenter(source: Bitmap, centerX: Int, centerY: Int, cropWidth: Int, cropHeight: Int): Bitmap {
            // Calculate the left, top, right, and bottom edges of the crop area
            val halfWidth = cropWidth / 2
            val halfHeight = cropHeight / 2

            val left = (centerX - halfWidth).coerceIn(0, source.width)
            val top = (centerY - halfHeight).coerceIn(0, source.height)
            val right = (centerX + halfWidth).coerceIn(0, source.width)
            val bottom = (centerY + halfHeight).coerceIn(0, source.height)

            // Calculate the width and height of the cropped area (may be smaller than requested if out of bounds)
            val finalWidth = right - left
            val finalHeight = bottom - top

            // Create and return the cropped bitmap
            return Bitmap.createBitmap(source, left, top, finalWidth, finalHeight)
        }

        /**
         * Resizes a bitmap to the specified target width while maintaining its aspect ratio.
         *
         * @param bitmap The original bitmap to be resized.
         * @param targetWidth The target width to resize the bitmap to.
         * @return A new bitmap resized to the target width while maintaining the aspect ratio of the original bitmap.
         */
        fun resizeMaintainingAspectRatio(bitmap: Bitmap, targetWidth: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height

            val aspectRatio = height.toFloat() / width.toFloat()
            val targetHeight = (targetWidth * aspectRatio).toInt()

            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        }

        fun createTransparent(width: Int = 1, height: Int = 1): Bitmap {
            // Create a bitmap with the specified width and height, and a configuration that supports transparency
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Create a canvas to draw on the bitmap
            val canvas = Canvas(bitmap)

            // Create a paint object with transparency
            val paint = Paint().apply {
                color = Color.TRANSPARENT
            }

            // Draw a transparent rectangle over the entire bitmap area
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            return bitmap
        }
    }
}




