package com.isdavid.machine_vision.yolo.views

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.yolo.views.delegates.AspectRationKeeper
import com.isdavid.machine_vision.yolo.views.delegates.AspectRationKeeperC


class AspectRatioKeeperTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextureView(context, attrs, defStyle) {
    private val aspectRationKeeper: AspectRationKeeperC = AspectRationKeeper()

    init {
        setWillNotDraw(false)
    }

    val shape: PlaneShape
        get() = aspectRationKeeper.shape

    fun setAspectRatio(planeShape: PlaneShape) {
        aspectRationKeeper.setShape(planeShape)

        post {
            invalidate()
            requestLayout()
        }
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
}



