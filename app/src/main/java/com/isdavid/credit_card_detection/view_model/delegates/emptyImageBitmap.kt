package com.isdavid.credit_card_detection.view_model.delegates

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint

fun emptyImageBitmap(width: Int = 1, height: Int = 1): ImageBitmap {
    val bitmap = ImageBitmap(width, height)

    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = Color.Black
    }

    paint.alpha = 0F

    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

    return bitmap
}