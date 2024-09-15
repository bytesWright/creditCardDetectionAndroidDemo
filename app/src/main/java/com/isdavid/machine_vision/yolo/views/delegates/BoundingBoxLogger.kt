package com.isdavid.machine_vision.yolo.views.delegates

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.compose.ui.unit.dp
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.camera.invert
import com.isdavid.machine_vision.camera.scaleTo
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBox
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBoxes

interface BoundingBoxLoggerC {
    var results: DetectionBoundingBoxes
    fun drawBoundingBoxes(canvas: Canvas, canvasShape: PlaneShape, sourceShape: PlaneShape?)
}

class BoundingBoxLogger : BoundingBoxLoggerC {
    private var _results = listOf<DetectionBoundingBox>()

    private val boxPaint = Paint()
    private val strokeWidth = 10.dp.value

    private val bounds = Rect()
    private val boundingRectTextPadding = 10.dp.value
    private val textSize = 50.dp.value
    private val opacity = 150

    private val textBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@BoundingBoxLogger.textSize // Adjust the text size as needed
        setARGB(opacity, 250, 250, 250)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@BoundingBoxLogger.textSize // Adjust the text size as needed
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override var results: DetectionBoundingBoxes
        get() = _results
        set(value) {
            _results = value
        }

    init {
        boxPaint.setARGB(opacity, 250, 250, 250)
        boxPaint.style = Paint.Style.STROKE;
        boxPaint.strokeWidth = strokeWidth;
    }

    override fun drawBoundingBoxes(
        canvas: Canvas,
        canvasShape: PlaneShape,
        sourceShape: PlaneShape?
    ) {
        val (shape, offset) = if (sourceShape != null) {
            val reshaped = sourceShape
                .invert()
                .scaleTo(height = canvasShape.height)

            val offset = (reshaped.width - canvasShape.width) / 2

            Pair(reshaped, offset)
        } else {
            Pair(canvasShape, 0)
        }

        _results.forEach {
            drawBoundingBox(canvas, it, shape, offset)
        }
    }


    private fun drawBoundingBox(
        canvas: Canvas, detectionBoundingBox: DetectionBoundingBox,
        canvasShape: PlaneShape,
        offset: Int
    ) {

        val (width, height) = canvasShape

        val left = detectionBoundingBox.x1 * width - offset
        val top = detectionBoundingBox.y1 * height
        val right = detectionBoundingBox.x2 * width - offset
        val bottom = detectionBoundingBox.y2 * height

        canvas.drawRect(left, top, right, bottom, boxPaint)
        val title = detectionBoundingBox.className

        textBackgroundPaint.getTextBounds(title, 0, title.length, bounds)

        val textWidth = bounds.width()
        val textHeight = bounds.height()

        // Calculate the center x position for the text
        val textX = left + (right - left) / 2 - textWidth / 2
        val textY = bottom + textHeight + strokeWidth + boundingRectTextPadding / 2

        canvas.drawRect(
            textX - boundingRectTextPadding / 2,
            bottom + strokeWidth,
            textX + textWidth + boundingRectTextPadding / 2,
            bottom + textHeight + boundingRectTextPadding + strokeWidth,
            textBackgroundPaint
        )

        canvas.drawText(title, textX, textY, textPaint)
    }
}