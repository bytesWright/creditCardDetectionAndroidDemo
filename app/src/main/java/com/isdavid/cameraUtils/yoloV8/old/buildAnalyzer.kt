package com.isdavid.cameraUtils.yoloV8.old

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import java.util.concurrent.ExecutorService

/**
 * Builds an ImageAnalysis analyzer to process camera frames as bitmaps.
 *
 * This function sets up an ImageAnalysis instance that processes frames from the camera
 * and converts them into a Bitmap, which is then passed to the provided callback function.
 *
 * @param processImage A callback function that receives the processed Bitmap for further handling.
 * @return The configured ImageAnalysis instance.
 */
fun buildAnalyzer(cameraExecutor: ExecutorService, processImage: (image: Bitmap) -> Unit): ImageAnalysis {
    val imageAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()

    imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
        Log.d("DXXD", "imageProxy width ${imageProxy.width} height ${imageProxy.height}")

        val bitmapBuffer: Bitmap = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        // Copy the image data from the ImageProxy to the bitmap
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        // Apply transformations to the bitmap (rotate and flip horizontally)
        val matrix = Matrix().apply {
            // Rotate the image based on the rotation degrees
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            // Flip the image horizontally
//            postScale(
//                -1f,
//                1f,
//                imageProxy.width.toFloat(),
//                imageProxy.height.toFloat()
//            )
        }

        // Create a new bitmap with the applied transformations
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0,
            bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        // Pass the processed bitmap to the provided callback function
        processImage(rotatedBitmap)
    }

    return imageAnalyzer
}