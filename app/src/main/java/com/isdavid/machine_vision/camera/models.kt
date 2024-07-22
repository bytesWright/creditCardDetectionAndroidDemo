package com.isdavid.machine_vision.camera

import android.graphics.Bitmap
import android.util.Size
import com.isdavid.log.strRound
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.TensorProperties

data class CameraData(
    val cameraId: String,
    val facingId: Int?,
    val facing: String,
    val formats: List<FormatData>,
    val formatsById: Map<Int, FormatData>,
    val focalLengths: List<Float>,
    val orientation: Int?
) {
    fun hasFormat(formatId: Int) = formatsById.containsKey(formatId)

    companion object
}


data class FormatData(
    val id: Int, // Given by those declared in android.graphics.ImageFormat
    val name: String, val resolutions: List<PlaneShape>
)

data class PlaneShape(
    val width: Int,
    val height: Int,
) {
    val area by lazy { width.toDouble() * height.toDouble() }

    val aspectRatio by lazy {
        val gcdValue = gcd(width, height)
        if (gcdValue == 0) return@lazy -1
        val aspectWidth = width / gcdValue
        val aspectHeight = height / gcdValue

        "$aspectWidth:$aspectHeight"
    }

    val formFactor: Double by lazy {
        if (height == 0) -1.0 else width.toDouble() / height.toDouble()
    }

    val inverseFormFactor: Double by lazy {
        if (width == 0) -1.0 else height.toDouble() / width.toDouble()
    }

    override fun toString(): String {
        return "PS [$width, $height], FF ${formFactor.strRound(6)} IFF ${
            inverseFormFactor.strRound(
                3
            )
        } AR $aspectRatio A $area"
    }

    operator fun minus(other: PlaneShape): PlaneShape {
        return PlaneShape(
            width = this.width - other.width,
            height = this.height - other.height
        )
    }


    companion object
}

fun PlaneShape.scaleTo(
    factor: Double? = null,
    width: Int? = null,
    height: Int? = null
): PlaneShape {
    return when {
        width != null -> PlaneShape(width, (this.height / this.formFactor).toInt())
        height != null -> PlaneShape((height * this.formFactor).toInt(), height)

        else -> PlaneShape(
            (this.width * (factor ?: 1.0)).toInt(),
            (this.height * (factor ?: 1.0)).toInt(),
        )
    }
}
fun PlaneShape.invert() = PlaneShape(this.height, this.width)

fun PlaneShape.toSize() = Size(this.width, this.height)
fun PlaneShape.Companion.from(bitmap: Bitmap) = PlaneShape(bitmap.width, bitmap.height)
fun PlaneShape.Companion.from(tensorProperties: TensorProperties) =
    PlaneShape(tensorProperties.width, tensorProperties.height)

fun gcd(width: Int, height: Int): Int {
    return if (height == 0) width else gcd(height, width % height)
}