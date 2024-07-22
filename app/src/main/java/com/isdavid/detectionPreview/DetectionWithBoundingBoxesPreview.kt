package com.isdavid.detectionPreview

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.isdavid.cameraUtils.yoloV8.OverlayView
import com.isdavid.cameraUtils.yoloV8.boundingBox.BoundingBox

@Composable
fun DetectionWithBoundingBoxesPreview(surface: View, boundingBoxes: List<BoundingBox>, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AndroidView(modifier = modifier
            .fillMaxSize()
            .background(Color.Red),
            factory = {
                surface
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