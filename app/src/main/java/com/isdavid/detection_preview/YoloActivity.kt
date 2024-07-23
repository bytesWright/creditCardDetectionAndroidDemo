package com.isdavid.detection_preview

import android.Manifest
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.isdavid.common.handlers.LoopHandlerWrapper
import com.isdavid.detection_preview.compose.YoloCompose
import com.isdavid.log.Logger
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.yolo.YoloVideoCapture
import com.isdavid.machine_vision.yolo.buildYoloCaptureWrapper
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import com.isdavid.machine_vision.yolo.views.AspectRatioKeeperTextureView
import com.isdavid.machine_vision.yolo.views.retrieveSurface
import com.isdavid.ui.theme.TensorflowLiteTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val log = Logger.provide("YLV")


class YoloActivity : ComponentActivity() {
    private val generalCameraTask = LoopHandlerWrapper("generalCameraTask")
    private var yoloVideoCapture: YoloVideoCapture? = null
    private lateinit var previewSurface: AspectRatioKeeperTextureView
    private val cameraReady = mutableStateOf(false)

    private val viewModel: YoloVM by viewModels {
        YoloVMF(application)
    }

    override fun onStart() {
        super.onStart()

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay?.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        log.line { "Screen Size ${PlaneShape(width, height)}" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        previewSurface = AspectRatioKeeperTextureView(this)

        val cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    cameraPermissionsGiven()
                } else {
                    // Handle the case where the user denied the permission
                }
            }

        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun cameraPermissionsGiven() {
        setContent {
            TensorflowLiteTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    YoloLogPreview(
//                        modifier = Modifier.padding(innerPadding),
//                        boundingBoxes = viewModel.boundingBoxes.value,
//                        surfaceView = previewSurface,
//                    )

                    YoloCompose(
                        surfaceView = previewSurface,
                        modifier = Modifier.padding(innerPadding),
                        startCamera = { startCamera() },
                        stopCamera = { stopCamera() },
                        cameraReady = cameraReady.value,
                        boundingBoxes = viewModel.boundingBoxes.value,
                        logImage = viewModel.imageLog.value
                    )
                }
            }
        }

//        startCamera()
    }

    private fun startCamera() =
        lifecycleScope.launch(Dispatchers.Main) {
            val tflModelWrapper: TflModelWrapper = viewModel.tflModelWrapper

            this@YoloActivity.yoloVideoCapture = buildYoloCaptureWrapper(
                baseContext,
                tflModelWrapper,
                generalCameraTask.handler,
                onFirstCapture = {
                    cameraReady.value = true
                },
                buildSurface = {
                    previewSurface.setAspectRatio(it)
                    runBlocking {
                        previewSurface.retrieveSurface(it)
                    }
                }
            )
        }

    private fun stopCamera() {
        this.yoloVideoCapture?.close()
        cameraReady.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        generalCameraTask.quitSafely()
        stopCamera()
    }
}


