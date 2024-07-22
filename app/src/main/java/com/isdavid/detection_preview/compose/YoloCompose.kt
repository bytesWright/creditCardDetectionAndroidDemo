package com.isdavid.detection_preview.compose

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBoxes
import com.isdavid.machine_vision.yolo.views.AspectRatioKeeperTextureView
import kotlin.math.max

@Composable
fun YoloCompose(
    surfaceView: AspectRatioKeeperTextureView,
    boundingBoxes: BoundingBoxes,
    startCamera: () -> Unit,
    stopCamera: () -> Unit,
    cameraReady: Boolean,
    modifier: Modifier = Modifier,
    logImage: Bitmap
) {
    val radius by animateFloatAsState(
        targetValue = if (cameraReady) 2f else .0001f,
        animationSpec = tween(
            durationMillis = if (cameraReady) 1000 else 200,
            delayMillis = 2000
        ),
        label = "CameraMask"
    )

    Box(modifier = modifier.fillMaxSize()) {
        YoloLogPreview(
            surfaceView = surfaceView,
            boundingBoxes = boundingBoxes
        )

        BottomButtons(startCamera, stopCamera)

        Image(
            modifier = Modifier.width(200.dp),
            bitmap = logImage.asImageBitmap(),
            contentDescription = "log"
        )
    }
}


@Composable
fun MaskedBox(
    radius: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
//            .fillMaxSize()
            .background(Color.Black)
            .width(100.dp)
            .height(100.dp)
            .drawWithCache {
                val gradient = Brush.radialGradient(
                    0.8f to Color.Transparent,
                    1f to Color.Black,
                    center = Offset(size.width / 2, size.height / 2),
                    radius = .5f * max(size.width, size.height)
                )

                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = gradient,
                        blendMode = BlendMode.DstIn
                    )
                }
            }
    )
}

@Composable
fun BottomButtons(
    startCamera: () -> Unit,
    stopCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Button(onClick = { startCamera() }) {
                Text(text = "Start Camera")
            }
            Button(onClick = { stopCamera() }) {
                Text(text = "Stop Camera")
            }
        }
    }
}
