package com.isdavid.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isdavid.log.buildQuickLogger
import com.isdavid.common.readFileAsLines
import com.isdavid.machine_vision.yolo.model_wrapper.TensorflowRawModel
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.GpuStatus
import com.isdavid.log.msString
import com.isdavid.log.strRound
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.support.common.FileUtil
import java.io.InputStream
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime


@RunWith(AndroidJUnit4::class)
class MultipleDetectionsTest {
    private lateinit var context: Context
    private lateinit var yoloV8ModelWrapper: TflModelWrapper
    private val log = buildQuickLogger("MDT") //model detection test

    private val config = object {
        val modulePath = "ccod/v0.3/model-float-16.tflite"
        val labelsPath = "ccod/v0.3/labels.txt"
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        val initializationTime = measureTime {
            val model: TensorflowRawModel = FileUtil.loadMappedFile(context, config.modulePath)
            val labels: MutableList<String> = readFileAsLines(context, config.labelsPath)
            val gpuAvailable = GpuStatus(context).available

            yoloV8ModelWrapper = TflModelWrapper(
                model,
                labels,
                gpuAvailable
            )
        }

        log("Initialization time ${initializationTime.msString()}")
    }

    @Test
    fun testSustainedPerformance() {
        // Load a test image from assets or resources
        val inputStream: InputStream = context.assets.open("testAssets/creditCardSample.png")
        val testBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        yoloV8ModelWrapper.onDetect = { _, _, _, _, _ -> }
        val detections = 240

        val multipleDetectionsTime: Duration = measureTime {
            for (number in 0 until detections) {
                yoloV8ModelWrapper.detect(
                    testBitmap
                )
            }
        }

        val iterationTime =
            multipleDetectionsTime.toDouble(DurationUnit.MILLISECONDS) / detections.toDouble()

        val fps = 1000 / iterationTime

        log("Total time: ${multipleDetectionsTime.msString()}.")
        log("Iteration time ${iterationTime.strRound()}ms")
        log("Iteration fps ${fps.strRound()}hz")
    }
}

