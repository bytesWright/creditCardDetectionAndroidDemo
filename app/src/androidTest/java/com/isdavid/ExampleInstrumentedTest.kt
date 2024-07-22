package com.isdavid

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isdavid.cameraUtils.yoloV8.YoloV8ModelWrapper
import java.io.InputStream
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class YoloV8ModelWrapperInternalActivity {
    private lateinit var context: Context
    private lateinit var yoloV8ModelWrapper: YoloV8ModelWrapper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        yoloV8ModelWrapper = YoloV8ModelWrapper(
            context = context,
            modelPath = "yoloV8/model16.tflite",
            labelPath = "yoloV8/labels.txt",
            tryToGpuAccelerate = true,
            onDetection = { source, boundingBoxes, time ->
                Log.d("MODEL_TEST", "inferenceTime $time")
                Log.d("MODEL_TEST", "boundingBoxes.size ${boundingBoxes.size}")
            }
        )
    }

    @Test
    fun testDetect() {
        // Load a test image from assets or resources
        val inputStream: InputStream = context.assets.open("test.png")
        val testBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        // Run the detection
        yoloV8ModelWrapper.detect(testBitmap)
    }
}