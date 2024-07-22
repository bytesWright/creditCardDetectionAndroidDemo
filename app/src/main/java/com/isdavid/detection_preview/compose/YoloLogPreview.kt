package com.isdavid.detection_preview.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBoxes
import com.isdavid.machine_vision.yolo.views.AspectRatioKeeperTextureView
import com.isdavid.machine_vision.yolo.views.YoloLogOverLayView

@Composable
fun YoloLogPreview(
    surfaceView: AspectRatioKeeperTextureView,
    modifier: Modifier = Modifier,
    boundingBoxes: BoundingBoxes
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
    ) {
        AndroidView(
            factory = { surfaceView },
            modifier = Modifier
                .align(Alignment.Center)
        )
        AndroidView(
            factory = {
                YoloLogOverLayView(context)
            },
            update = {
                it.setAspectRatio(surfaceView.shape)
                it.results = boundingBoxes
            },
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

