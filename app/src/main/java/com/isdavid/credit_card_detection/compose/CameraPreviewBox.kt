package com.isdavid.credit_card_detection.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBoxes
import com.isdavid.machine_vision.yolo.views.YoloLogSurfaceView

@Composable
fun CameraPreviewBox(
    surfaceView: YoloLogSurfaceView,
    detectionBoundingBoxes: DetectionBoundingBoxes,
    modifier: Modifier = Modifier,
    viewLog: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { surfaceView },
            update = {
                if (viewLog) {
                    it.results = detectionBoundingBoxes
                    it.invalidate()
                }
            }
        )
    }
}