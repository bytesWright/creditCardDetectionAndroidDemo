package com.isdavid.machine_vision.yolo.views.delegates


import com.isdavid.log.Logger
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.yolo.views.log
import kotlin.math.roundToInt

val log = Logger.provide("YLV")

interface AspectRationKeeperC {
    fun correctSize(width: Int, height: Int): Pair<Int, Int>
    fun setShape(planeShape: PlaneShape)
    val shape: PlaneShape
}

class AspectRationKeeper : AspectRationKeeperC {
    private var _planeShape: PlaneShape = PlaneShape(0, 0)

    override val shape: PlaneShape
        get() = _planeShape

    override fun setShape(planeShape: PlaneShape) {
        this._planeShape = planeShape

        log.line {
            """
            Set aspectRatio ${planeShape.formFactor}
                $planeShape
            """.trimIndent()
        }
    }

    override fun correctSize(width: Int, height: Int): Pair<Int, Int> {
        val formFactor = _planeShape.formFactor
        if (_planeShape.formFactor == 0.0) {
            log.line { "    Correct Size NO $formFactor" }
            return Pair(width, height)
        } else {
            val newWidth: Int
            val newHeight: Int

            val actualRatio = if (width > height) formFactor else 1f / formFactor

            if (width < height * actualRatio) {
                newHeight = height
                newWidth = (height.toFloat() * actualRatio).roundToInt()
            } else {
                newWidth = width
                newHeight = (width.toFloat() / actualRatio).roundToInt()
            }

            log.line {
                """
                |    Input shape     ${PlaneShape(width, height)}
                |    Output shape    ${PlaneShape(newWidth, newHeight)}
                |    Selected FF     $actualRatio
                |    
                """.trimMargin()
            }

            return Pair(newWidth, newHeight)
        }
    }
}