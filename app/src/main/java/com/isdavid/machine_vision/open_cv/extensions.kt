package com.isdavid.machine_vision.open_cv

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar

operator fun Mat.minus(b: Mat): Mat {
    val resultMat = Mat(this.rows(), this.cols(), CvType.CV_32FC3)
    Core.subtract(this, b, resultMat)
    return resultMat
}

operator fun Mat.times(b: Mat): Mat {
    val resultMat = Mat(this.rows(), this.cols(), CvType.CV_32FC3)
    Core.multiply(this, b, resultMat)
    return resultMat
}

//

fun Mat.mean(): Scalar = Core.mean(this)

fun Mat.absDiff(b: Mat): Mat {
    val resultMat = Mat(this.rows(), this.cols(), CvType.CV_32FC3)
    Core.absdiff(this, b, resultMat)
    return resultMat
}

fun Mat.mse(b: Mat) = this.absDiff(b).mean()

fun Mat.isSimilar(b: Mat, threshold: Double) = this.absDiff(b).mean() < threshold

fun Mat.split(rows: Int, cols: Int): List<Mat> {
    // Check for valid input
    if (rows <= 0 || cols <= 0) {
        throw IllegalArgumentException("Rows and columns must be positive integers")
    }

    // Get image height and width
    val imageHeight = rows()
    val imageWidth = cols()

    // Calculate size of each sub-image
    val subImageHeight = imageHeight / rows
    val subImageWidth = imageWidth / cols

    // List to store sub-images
    val subImages = mutableListOf<Mat>()

    // Loop through each row and column
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            // Calculate top-left corner coordinates of the sub-image
            val xStart = j * subImageWidth
            val yStart = i * subImageHeight

            // Calculate bottom-right corner coordinates (consider edge cases)
            val xEnd = minOf(xStart + subImageWidth, imageWidth)
            val yEnd = minOf(yStart + subImageHeight, imageHeight)

            // Create a ROI (Region of Interest) for the sub-image
            val roi = Rect(xStart, yStart, xEnd - xStart, yEnd - yStart)

            // Extract the sub-image using the ROI
            val subImage = Mat(this, roi)

            // Add sub-image to the list
            subImages.add(subImage)
        }
    }

    return subImages
}

//

fun List<Mat>.mse(b: List<Mat>): List<Scalar> = mapIndexed { i, sr -> sr.mse(b[i]) }

fun List<Mat>.isSimilar(b: List<Mat>, threshold: Double): Boolean = mapIndexed { i, sr ->
    sr.isSimilar(b[i], threshold)
}.find { !it } ?: true

//

operator fun Scalar.compareTo(value: Double): Int {
    // Loop through all components of the Scalar
    var equal = true

    for (i in 0 until `val`.size) {
        if (`val`[i] > value) {
            return 1
        }
        equal = equal && (`val`[i] == value)
    }

    return if (equal) 0 else -1
}

fun List<Scalar>.flatten(): List<Double> {
    if (isEmpty()) return emptyList()

    // Calculate the total size of the flattened list
    val totalSize = size * this[0].`val`.size
    val flattenedList = MutableList(totalSize) { 0.0 }

    // Fill the flattened list with components of the Scalars
    var index = 0
    for (scalar in this) {
        for (component in scalar.`val`) {
            flattenedList[index] = component
            index += 1
        }
    }

    return flattenedList
}