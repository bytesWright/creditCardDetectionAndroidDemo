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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.support.common.FileUtil
import java.io.InputStream
import kotlin.time.measureTime


@RunWith(AndroidJUnit4::class)
class SingleDetectionTest {
    private val log = buildQuickLogger("MDT") //model detection test

    private lateinit var context: Context
    private lateinit var yoloV8ModelWrapper: TflModelWrapper

    private val config = object {
        val modulePath = "ccod/v0.1/model-float-16.tflite"
        val labelsPath = "ccod/v0.1/labels.txt"
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

        log("initialization time ${initializationTime.msString()}")
    }

    @Test
    fun testDetect() {
        // Load a test image from assets or resources
        val inputStream: InputStream = context.assets.open("testAssets/creditCardSample.png")
        val testBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        yoloV8ModelWrapper.onDetect = { _, boundingBoxes, _, _, time ->
            log("detection time ${time.msString()}")
            log("boundingBoxes.size ${boundingBoxes.size}")
        }

        // Run the detection
        yoloV8ModelWrapper.detect(
            testBitmap
        )
    }
}