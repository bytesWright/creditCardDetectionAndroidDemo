package com.isdavid.credit_card_detection

import android.Manifest
import android.os.Bundle
import android.util.Log
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
import com.isdavid.credit_card_detection.compose.CreditCardDetectionCompose
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMF
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionViewModel
import com.isdavid.machine_vision.yolo.YoloVideoCapture
import com.isdavid.machine_vision.yolo.buildYoloCaptureWrapper
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import com.isdavid.machine_vision.yolo.views.YoloLogSurfaceView
import com.isdavid.machine_vision.yolo.views.retrieveSurface
import com.isdavid.ui.theme.TensorflowLiteTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.opencv.android.OpenCVLoader

class CreditCardDetectionActivity : ComponentActivity() {
    private val generalCameraTask = LoopHandlerWrapper("generalCameraTask")
    private var yoloVideoCapture: YoloVideoCapture? = null
    private lateinit var previewSurface: YoloLogSurfaceView
    private val cameraReady = mutableStateOf(false)

    private val viewModel: CreditCardDetectionViewModel by viewModels {
        CreditCardDetectionVMF(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.stopCamera = ::stopCamera
        previewSurface = YoloLogSurfaceView(this)

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
                    CreditCardDetectionCompose(
                        surfaceView = previewSurface,
                        viewModel = viewModel,
                        cameraReady = cameraReady.value,
                        startCamera = { startCamera() },
                        stopCamera = { stopCamera() },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }

//        startCamera()
    }

    private fun startCamera() = lifecycleScope.launch(Dispatchers.Main) {
        viewModel.setCaptureMode(
            CreditCardDetectionVMC.Mode.BOTH
        )

        viewModel.clearValues()


        val tflModelWrapper: TflModelWrapper = viewModel.tflModelWrapper

        this@CreditCardDetectionActivity.yoloVideoCapture = buildYoloCaptureWrapper(
            baseContext,
            tflModelWrapper,
            generalCameraTask.handler,
            onFirstCapture = { cameraReady.value = true },
            buildSurface = {
                runBlocking {
                    previewSurface.setAspectRatio(it)
                    previewSurface.retrieveSurface()
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
    }
}


