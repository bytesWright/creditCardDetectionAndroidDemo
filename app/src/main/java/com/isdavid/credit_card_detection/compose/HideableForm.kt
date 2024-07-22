package com.isdavid.credit_card_detection.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC
import kotlin.math.max

@Composable
fun HideableForm(
    modifier: Modifier = Modifier,
    cameraReady: Boolean = false,
    capture: () -> Unit = {},
    maskRadius: Float,
    cameraFullyDisplayed: Boolean,
    viewModel: CreditCardDetectionVMC
) {
    val backGroundColor = MaterialTheme.colorScheme.background

    if (!cameraFullyDisplayed) CreditCardForm(
        capture = capture,
        viewModel = viewModel,
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val gradient = Brush.radialGradient(
                    0.8f to Color.Transparent,
                    1f to backGroundColor,
                    center = Offset(size.width / 2, size.height / 2),
                    radius = maskRadius * max(size.width, size.height)
                )

                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = gradient, blendMode = BlendMode.DstIn
                    )
                }
            }
            .background(backGroundColor))
}