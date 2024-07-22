package com.isdavid.machine_vision.yolo.model_wrapper.tensorflow

import android.graphics.Bitmap
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.camera.from
import java.nio.ByteBuffer
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

fun prepareInterpreterInput(cameraFrame: Bitmap, tensorProperties: TensorProperties, imageProcessor: ImageProcessor): Pair<PlaneShape, ByteBuffer> {
    val resizedBitmap = Bitmap.createScaledBitmap(
        cameraFrame, tensorProperties.width, tensorProperties.height, false
    )

    val tensorImage = TensorImage(DataType.FLOAT32)
    tensorImage.load(resizedBitmap)

    val processedImage = imageProcessor.process(tensorImage)
    val imageBuffer = processedImage.buffer

    val shape = PlaneShape.from(resizedBitmap)
    resizedBitmap.recycle()
    return Pair(shape, imageBuffer)
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

