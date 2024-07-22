package com.isdavid.credit_card_detection.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.isdavid.credit_card_detection.view_model.delegates.emptyImageBitmap

@Composable
fun CreditCardImageField(
    modifier: Modifier = Modifier,
    description: String = "",
    imageBitmap: ImageBitmap = emptyImageBitmap(),
) {
    var width by remember { mutableStateOf(0.dp) }
    var height by remember { mutableStateOf(0.dp) }

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp),
            )
            .onGloballyPositioned { coordinates ->
                with(density) {
                    width = coordinates.size.width.toDp()
                    height = width / 2
                }
            }
            .height(height)
    ) {

        // Replace with your image painter
        Image(
            bitmap = imageBitmap,
            contentDescription = description,
            modifier = Modifier
                .width(width)
                .height(height)
        )
        Text(
            text = description,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        )
    }
}