package com.isdavid.machine_vision.yolo.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceView
import com.isdavid.log.Logger
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBoxes
import com.isdavid.machine_vision.yolo.views.delegates.AspectRationKeeper
import com.isdavid.machine_vision.yolo.views.delegates.AspectRationKeeperC
import com.isdavid.machine_vision.yolo.views.delegates.BoundingBoxLogger
import com.isdavid.machine_vision.yolo.views.delegates.BoundingBoxLoggerC

val log = Logger.provide("YLV")

class YoloLogSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val boundingBoxLogger: BoundingBoxLogger = BoundingBoxLogger(),
) : SurfaceView(context, attrs, defStyle),
    BoundingBoxLoggerC by boundingBoxLogger {
    private val aspectRationKeeper: AspectRationKeeperC = AspectRationKeeper()

    init {
        setWillNotDraw(false)
    }

    override var results: BoundingBoxes
        get() = boundingBoxLogger.results
        set(value) {
            boundingBoxLogger.results = value
            invalidate()
        }

    fun setAspectRatio(planeShape: PlaneShape) {
        aspectRationKeeper.setShape(planeShape)
        holder.setFixedSize(planeShape.width, planeShape.height)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        log.line { "On measure:" }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val (newWidth, newHeight) = aspectRationKeeper.correctSize(width, height)

        log.line {
            """
            |    Spec         $widthMeasureSpec $heightMeasureSpec
            |    Measured     ${PlaneShape(width, height)}
            |    Final shape  ${PlaneShape(newWidth, newHeight)}
        """.trimMargin()
        }

        setMeasuredDimension(newWidth, newHeight)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawBoundingBoxes(canvas, PlaneShape(width, height), null)
    }
}



