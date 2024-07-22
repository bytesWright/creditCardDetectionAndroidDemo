package com.isdavid.cameraUtils.yoloV8

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.isdavid.cameraUtils.yoloV8.boundingBox.BoundingBox
import com.isdavid.cameraUtils.yoloV8.boundingBox.computeBoundingBoxesX
import com.isdavid.cameraUtils.yoloV8.boundingBox.filterWithNms
import com.isdavid.common.readFileAsLines
import kotlin.time.Duration
import kotlin.time.measureTimedValue
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor

typealias YoloV8onDetect = (sourceImage: Bitmap, boundingBoxes: List<BoundingBox>, executionTime: Duration) -> Unit

/**
 * YoloV8TensorflowLight class for performing object detection using TensorFlow Lite model.
 */
class YoloV8ModelWrapper(
    context: Context,
    modelPath: String = "yoloV8/model16.tflite",
    labelPath: String = "yoloV8/labels.txt",
    private val tryToGpuAccelerate: Boolean = true,
    private val onDetection: YoloV8onDetect
) {
    companion object {
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3F
        private const val IOU_THRESHOLD = 0.5F
    }

    private val interpreter: Interpreter
    private var labels = mutableListOf<String>()

    val tensorProperties: TensorProperties

    private val imageProcessor = ImageProcessor
        .Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    init {
        val options = Interpreter
            .Options()
            .apply {
                numThreads = Runtime.getRuntime().availableProcessors() / 3
                if (shouldGpuAccelerate(context)) addDelegate(GpuDelegate())
            }

        val model = FileUtil.loadMappedFile(context, modelPath)

        interpreter = Interpreter(model, options)
        tensorProperties = TensorProperties.buildFromInterpreter(interpreter)
        labels = readFileAsLines(context, labelPath)
    }

    fun detect(inputBitmap: Bitmap): List<BoundingBox> {
        val tensorProperties = tensorProperties ?: return emptyList()

        val (boundingBoxes, executionTime) = measureTimedValue {
            val imageBuffer = prepareInterpreterInput(
                inputBitmap,
                tensorProperties,
                imageProcessor
            )

            val output = prepareOutput(tensorProperties)
            interpreter.run(imageBuffer, output.buffer)

            val allBoundingBoxes = computeBoundingBoxesX(
                output.floatArray,
                tensorProperties,
                CONFIDENCE_THRESHOLD,
                labels
            )

            filterWithNms(
                allBoundingBoxes,
                IOU_THRESHOLD
            )
        }

        onDetection(inputBitmap, boundingBoxes, executionTime)
        return boundingBoxes
    }

    private fun shouldGpuAccelerate(context: Context): Boolean {
        if (!tryToGpuAccelerate) return false

        val packageManager = context.packageManager
        val feature = PackageManager.FEATURE_OPENGLES_EXTENSION_PACK
        return packageManager.hasSystemFeature(feature)
    }

    fun close() {
        interpreter.close()
    }
}

