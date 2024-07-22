package com.isdavid.cameraUtils.cameraData

import android.util.Size

data class CameraData(
    val cameraId: String,
    val lensOrientationId: Int?,
    val lensOrientation: String,
    val formats: List<FormatData>,
    val formatsById: Map<Int, FormatData>,
    val focalLengths: List<Float>
) {
    fun hasFormat(formatId: Int) = formatsById.containsKey(formatId)
    companion object
}


data class FormatData(
    val id: Int, // Given by those declared in android.graphics.ImageFormat
    val name: String,
    val resolutions: List<Resolution>
)

data class Resolution(
    val width: Int,
    val height: Int,
    val area: Double
) {
    fun toSize(): Size {
        return Size(this.width, this.height)
    }
}