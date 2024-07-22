package com.isdavid

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.isdavid.ui.theme.TensorflowLiteTestTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class
MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: DetectionViewModel by lazy {
        DetectionViewModel(baseContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    setContent {
                        TensorflowLiteTestTheme {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                ObjectDetectionView(
                                    viewModel,
                                    cameraExecutor
                                )
                            }
                        }
                    }
                } else {
                    // Handle the case where the user denied the permission
                }
            }

        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

