package com.isdavid.machine_vision.yolo.model_wrapper

import android.graphics.Bitmap
import com.isdavid.log.Logger
import com.isdavid.machine_vision.BitmapOperations
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.camera.from
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBoxes
import com.isdavid.machine_vision.yolo.boundingBox.computeBoundingBoxesX
import com.isdavid.machine_vision.yolo.boundingBox.filterWithNms
import com.isdavid.machine_vision.yolo.bundles.CameraStatus
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.TensorProperties
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.prepareInterpreterInput
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.prepareOutput
import java.nio.MappedByteBuffer
import kotlin.time.Duration
import kotlin.time.measureTimedValue
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor

typealias YoloOnDetect = (
    sourceBitmap: Bitmap,
    detectionBoundingBoxes: DetectionBoundingBoxes,
    cameraStatus: CameraStatus?,
    controlRemote: TflModelRemoteControl,
    executionTime: Duration,
) -> Unit

typealias TensorflowRawModel = MappedByteBuffer


class TflModelWrapper(
    model: TensorflowRawModel,
    private val labels: List<String>,
    private val gpuAccelerate: Boolean = true
) {
    companion object {
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3F
        private const val IOU_THRESHOLD = 0.5F
    }

    val log = Logger(
        "TFLM",
//        talk = false,
        levelLimit = 3
    )

    var onDetect: YoloOnDetect? = null
    var onLogImage: (bitmap: Bitmap) -> Unit = { _ -> }

    private val interpreter: Interpreter
    private val tensorProperties: TensorProperties
    private val controlRemote = TflModelRemoteControl(this)
    val paused get() = _paused
    private var _paused = false

    val closed get() = _closed
    private var _closed = false

    private val imageProcessor = ImageProcessor
        .Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    init {
        log.line(message = "Started")

        val options = Interpreter
            .Options()
            .apply {
                numThreads = Runtime.getRuntime().availableProcessors() / 2
                if (gpuAccelerate) addDelegate(GpuDelegate())
            }

        interpreter = Interpreter(model, options)
        tensorProperties = TensorProperties.buildFromInterpreter(interpreter)

        log.block(level = 1) {
            listOf(
                "    Gpu Accelerate $gpuAccelerate",
                "    Num Threads ${options.numThreads}",
                "    Tensor properties $tensorProperties",
                "    Created interpreter"
            )
        }
    }

    fun detect(inputBitmap: Bitmap, cameraStatus: CameraStatus? = null): DetectionBoundingBoxes {
        if (_paused || _closed) {
            log.line(4, message = "Ignoring input. I'm on pause or closed.")
            return emptyList()
        }

        val width = inputBitmap.width
        val height = inputBitmap.height

        val reshapedImage = BitmapOperations.cropFromCenter(
            inputBitmap,
            width / 2, height / 2,
            tensorProperties.width * 4, tensorProperties.height * 4
        )

        onLogImage(reshapedImage)

        val (result, executionTime) = measureTimedValue {
            val (reshape, imageBuffer) = prepareInterpreterInput(
                reshapedImage,
                tensorProperties,
                imageProcessor
            )

            log.line(5, message = "    ImageBuffer size ${imageBuffer.capacity()}")

            val output = prepareOutput(tensorProperties)
            interpreter.run(imageBuffer, output.buffer)

            val allBoundingBoxes = computeBoundingBoxesX(
                output.floatArray,
                tensorProperties,
                CONFIDENCE_THRESHOLD,
                labels
            )

            Pair(
                reshape, filterWithNms(
                    allBoundingBoxes,
                    IOU_THRESHOLD
                )
            )
        }

        val (reshape, boundingBoxes) = result

        log.block(level = 3, label = "shapes", limit = 1) {
            val bs = PlaneShape.from(inputBitmap)
            val ts = PlaneShape.from(tensorProperties)

            listOf(
                "Starting detection",
                "    Input shape  : $bs",
                "    Tensor shape : $ts}",
                "    Diff shape   : ${bs - ts}",
                "    Reshape      : $reshape"
            )
        }

        log.line(3, label = "detection", limit = 1, until = { boundingBoxes.size > 0 }) {
            "Match ${boundingBoxes.size}"
        }

        log.line(5, message = "Executed detection boundingBoxes ${boundingBoxes.size}")

        onDetect?.invoke(
            reshapedImage,
            boundingBoxes,
            cameraStatus,
            controlRemote,
            executionTime,
        )

        inputBitmap.recycle()
        return boundingBoxes
    }

    @Synchronized
    fun pause() {
        log.line(2, message = "Pause")
        _paused = true
    }

    @Synchronized
    fun resume() {
        log.line(2, message = "Resume")
        _paused = false
    }


    fun close() {
        log.line(0, message = "Closed")
        _closed = true
        interpreter.close()
    }
}


class TflModelRemoteControl(private val tflModelWrapper: TflModelWrapper) {
    val paused
        get() = tflModelWrapper.paused
    val closed
        get() = tflModelWrapper.closed

    fun pause() = tflModelWrapper.pause()
    fun resume() = tflModelWrapper.resume()
    fun close() = tflModelWrapper.pause()
}
