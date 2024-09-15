package com.isdavid.machine_vision.yolo.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.yolo.boundingBox.DetectionBoundingBoxes
import com.isdavid.machine_vision.yolo.views.delegates.BoundingBoxLogger
import com.isdavid.machine_vision.yolo.views.delegates.BoundingBoxLoggerC

class YoloLogOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val boundingBoxLogger: BoundingBoxLogger = BoundingBoxLogger(),
) : View(context, attrs, defStyle),
    BoundingBoxLoggerC by boundingBoxLogger {

    init {
        setWillNotDraw(false)
    }

    private var sourceShape: PlaneShape? = null

    fun setAspectRatio(planeShape: PlaneShape) {
        this.sourceShape = planeShape

        post {
            invalidate()
            requestLayout()
        }
    }

    override var results: DetectionBoundingBoxes
        get() = boundingBoxLogger.results
        set(value) {
            boundingBoxLogger.results = value
            invalidate()
        }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawBoundingBoxes(
            canvas,
            PlaneShape(width, height),
            sourceShape,
        )
    }
}



