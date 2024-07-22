package com.isdavid.machine_vision.yolo.model_wrapper.tensorflow

import org.tensorflow.lite.Interpreter
import java.util.InputMismatchException

data class TensorProperties(
    val width: Int,
    val height: Int,
    val dataBundleSize: Int,
    val predictionsLimit: Int,
    val formFactor: Int = width / height
) {
    companion object {
        fun buildFromInterpreter(interpreter: Interpreter): TensorProperties {
            // YOLO V8 input tensor shape is [1, 640, 640, 3]
            // 1: Means all the data is in one row
            // 640, 640: The size of the image (with, height)
            // 3: The amount of elements to hold a the information of a pixel
            // Total input size 1,228,800 elements
            val inputShape = interpreter.getInputTensor(0)?.shape() ?: throw InputMismatchException(
                "Could not get tensor input shape"
            )

            // YOLO V8 output tensor shape for the detection of the credit card and text is [1, 6, 8400]
            // 1: means all the data is in one row
            // 6: data bundle size, is the amount of elements that hold the data of each prediction.
            // it consist of x center, y center, width, height, class 1 conf, class 2 conf, ..., class N conf
            //
            // 8400: Is the limit of the detections
            // Total output size 50,400 elements
            val outputShape = interpreter.getOutputTensor(0)?.shape()
                ?: throw InputMismatchException("Could not get tensor output shape")

            return TensorProperties(
                width = inputShape[1],
                height = inputShape[2],
                dataBundleSize = outputShape[1], // confidence, class, center x, center y, width, height
                predictionsLimit = outputShape[2]
            )
        }
    }
}


