package com.isdavid.credit_card_detection.compose


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC
import com.isdavid.machine_vision.yolo.views.YoloLogSurfaceView

@Composable
fun CreditCardDetectionCompose(
    modifier: Modifier = Modifier,
    surfaceView: YoloLogSurfaceView,
    viewModel: CreditCardDetectionVMC,
    cameraReady: Boolean,
    startCamera: () -> Unit,
    stopCamera: () -> Unit
) {

    val transitionDuration = if (cameraReady) 800 else 200

    val maskRadius by animateFloatAsState(
        targetValue = if (cameraReady) 2f else .0001f,
        animationSpec = tween(durationMillis = transitionDuration),
        label = "CameraMask"
    )

    val cameraFullyDisplayed = maskRadius == 2f

    val alpha: Float by animateFloatAsState(
        targetValue = if (cameraReady) 1f else 0f,
        animationSpec = tween(durationMillis = transitionDuration),
        label = "captureInstructions"
    )

    Box(modifier = modifier) {
        CameraPreviewBox(
            detectionBoundingBoxes = viewModel.detectionBoundingBoxes.value,
            surfaceView = surfaceView
        )

        HideableForm(
            capture = startCamera,
            cameraReady = cameraReady,
            maskRadius = maskRadius,
            cameraFullyDisplayed = cameraFullyDisplayed,
            viewModel = viewModel,
        )

        if (cameraReady) InstructionsLayer(
            modifier = Modifier.alpha(alpha),
            viewModel = viewModel
        )
    }
}

