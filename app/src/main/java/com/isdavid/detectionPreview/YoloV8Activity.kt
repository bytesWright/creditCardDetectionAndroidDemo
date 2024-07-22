package com.isdavid.detectionPreview

import android.hardware.camera2.CameraDevice
import android.os.Build
import android.os.Bundle
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.isdavid.cameraUtils.yoloV8.YoloV8CaptureWrapper
import com.isdavid.cameraUtils.yoloV8.boundingBox.BoundingBox
import com.isdavid.cameraUtils.yoloV8.initYoloV8Capture
import com.isdavid.detectionPreview.ui.theme.TensorflowLiteTestTheme
import com.isdavid.handlers.LoopHandlerWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YoloV8Activity : ComponentActivity() {
    private val generalCameraTask = LoopHandlerWrapper("generalCameraTask")

    private val boundingBoxes = mutableStateOf(emptyList<BoundingBox>())
    private var yoloV8CaptureWrapper: YoloV8CaptureWrapper? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textureView = TextureView(this@YoloV8Activity)
        enableEdgeToEdge()

        setContent {
            TensorflowLiteTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetectionWithBoundingBoxesPreview(
                        surface = textureView,
                        modifier = Modifier.padding(innerPadding),
                        boundingBoxes = boundingBoxes.value
                    )
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val yoloV8Capture = initYoloV8Capture(baseContext, textureView, generalCameraTask.handler) { bitmap, boundingBoxes, _ ->
                this@YoloV8Activity.boundingBoxes.value = boundingBoxes
            }

            this@YoloV8Activity.yoloV8CaptureWrapper = yoloV8Capture
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        generalCameraTask.quitSafely()
    }
}


