package com.isdavid

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.ResolutionInfo
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.isdavid.cameraUtils.yoloV8.OverlayView
import com.isdavid.cameraUtils.yoloV8.old.buildAnalyzer
import java.util.concurrent.ExecutorService


@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun ObjectDetectionView(viewModel: DetectionViewModel, cameraExecutor: ExecutorService) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(LocalContext.current)
    val boundingBoxes = viewModel.boundingBoxes.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .background(Color.Red)
                .fillMaxSize(),

            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraSelector = CameraSelector
                    .Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val resolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                    .build()

                val preview: Preview = Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                val analyzer: ImageAnalysis = buildAnalyzer(cameraExecutor) {
                    viewModel.cardYoloV8ModelWrapper.detect(it)
                }

                try {
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        ctx as ComponentActivity,
                        cameraSelector,
                        preview,
                        analyzer
                    )

                } catch (exc: Exception) {
                    Log.e("DXXD", "Use case binding failed", exc)
                }

                previewView.scaleType = PreviewView.ScaleType.FIT_CENTER
                previewView
            }
        )

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x10FF0000)),
            factory = { ctx -> OverlayView(ctx) },
            update = {
                it.setResults(boundingBoxes)
            }
        )
    }
}


fun getAdjustedResolution(resolution: ResolutionInfo?): Pair<Int, Int>? {
    val res = resolution ?: return null

    val width = res.resolution.width
    val height = res.resolution.height
    return when (val rotationDegrees = res.rotationDegrees) {
        90, 270 -> Pair(height, width) // Swap width and height for 90 and 270 degrees rotation
        0, 180 -> Pair(width, height)  // Keep the original resolution for 0 and 180 degrees rotation
        else -> throw IllegalArgumentException("Invalid rotation degrees: $rotationDegrees")
    }
}