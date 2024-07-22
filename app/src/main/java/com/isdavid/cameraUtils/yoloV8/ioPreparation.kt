package com.isdavid.cameraUtils.yoloV8

import android.graphics.Bitmap
import java.nio.ByteBuffer
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

fun prepareInterpreterInput(cameraFrame: Bitmap, tensorProperties: TensorProperties, imageProcessor: ImageProcessor): ByteBuffer {
    val resizedBitmap = Bitmap.createScaledBitmap(
        cameraFrame, tensorProperties.width, tensorProperties.height, false
    )

    val tensorImage = TensorImage(DataType.FLOAT32)
    tensorImage.load(resizedBitmap)

    val processedImage = imageProcessor.process(tensorImage)
    val imageBuffer = processedImage.buffer

    return imageBuffer
}


fun prepareOutput(tensorProperties: TensorProperties, outputImageType: DataType = DataType.FLOAT32): TensorBuffer {
    val output = TensorBuffer.createFixedSize(
        intArrayOf(
            1,
            tensorProperties.dataBundleSize,
            tensorProperties.predictionsLimit
        ),
        outputImageType
    )
    return output
}

