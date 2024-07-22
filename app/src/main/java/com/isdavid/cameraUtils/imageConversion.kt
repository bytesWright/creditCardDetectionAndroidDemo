package com.isdavid.cameraUtils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import java.io.ByteArrayOutputStream
import java.util.*


class ImageConversion {
    companion object {
        fun imageToBitmap(image: Image): Bitmap {
            val data = imageToByteArray(image)
            return BitmapFactory.decodeByteArray(data, 0, data.size)
        }

        fun imageToByteArray(image: Image): ByteArray {
            if (image.format == ImageFormat.JPEG) {
                val planes = image.planes
                val buffer = planes[0].buffer
                val data = ByteArray(buffer.capacity())
                buffer[data]
                return data
            } else if (image.format == ImageFormat.YUV_420_888) {
                return nv21toJpegBytes(
                    yuv420888imageToNv21bytes(image),
                    image.width, image.height
                )
            }

            throw InputMismatchException("Image format not handled")
        }

        fun yuv420888imageToNv21bytes(image: Image): ByteArray {
            val nv21: ByteArray
            val yBuffer = image.planes[0].buffer
            val vuBuffer = image.planes[2].buffer

            val ySize = yBuffer.remaining()
            val vuSize = vuBuffer.remaining()

            nv21 = ByteArray(ySize + vuSize)

            yBuffer[nv21, 0, ySize]
            vuBuffer[nv21, ySize, vuSize]

            return nv21
        }

        fun nv21toJpegBytes(nv21: ByteArray, width: Int, height: Int): ByteArray {
            val out = ByteArrayOutputStream()
            val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
            return out.toByteArray()
        }
    }
}

class BitmapConversion {
    companion object {
        fun rotate(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }
}

