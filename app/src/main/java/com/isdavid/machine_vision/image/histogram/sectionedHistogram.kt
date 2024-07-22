package com.isdavid.machine_vision.image.histogram


import android.graphics.Bitmap
import android.graphics.Color


typealias HistogramChannel<T> = Array<T>
typealias Histogram<T> = Array<HistogramChannel<T>>
typealias SectionedHistogram<T> = Array<Array<Histogram<T>>>


fun calculateSectionedHistogram(bitmap: Bitmap, rows: Int, columns: Int): SectionedHistogram<Int> {
    val width = bitmap.width
    val height = bitmap.height

    val sectionWidth = width / columns
    val sectionHeight = height / rows

    // Initialize the 3D array to hold histograms for each section
    val histograms = Array(rows) {
        Array(columns) {
            arrayOf(
                Array(256) { 0 },
                Array(256) { 0 },
                Array(256) { 0 }
            )
        }
    }

    for (r in 0 until rows) {
        for (c in 0 until columns) {
            // Initialize histogram arrays for the current section
            val redHistogram = Array(256) { 0 }
            val greenHistogram = Array(256) { 0 }
            val blueHistogram = Array(256) { 0 }

            // Calculate the bounds of the current section
            val startX = c * sectionWidth
            val startY = r * sectionHeight
            val endX = Math.min(startX + sectionWidth, width)
            val endY = Math.min(startY + sectionHeight, height)

            // Iterate over pixels in the current section
            for (x in startX until endX) {
                for (y in startY until endY) {
                    val pixel = bitmap.getPixel(x, y)

                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    redHistogram[red]++
                    greenHistogram[green]++
                    blueHistogram[blue]++
                }
            }

            // Store the histograms of the current section
            histograms[r][c][0] = redHistogram
            histograms[r][c][1] = greenHistogram
            histograms[r][c][2] = blueHistogram
        }
    }

    return histograms
}


fun calculateSectionedHistogramNormalized(
    bitmap: Bitmap,
    rows: Int,
    columns: Int
): SectionedHistogram<Double> {
    val width = bitmap.width
    val height = bitmap.height

    val sectionWidth = width / columns
    val sectionHeight = height / rows

    // Initialize the 3D array to hold normalized histograms for each section
    val histograms = Array(rows) {
        Array(columns) {
            arrayOf(Array(256) { 0.0 }, Array(256) { 0.0 }, Array(256) { 0.0 })
        }
    }

    for (r in 0 until rows) {
        for (c in 0 until columns) {
            // Initialize histogram arrays for the current section
            val redHistogram = IntArray(256)
            val greenHistogram = IntArray(256)
            val blueHistogram = IntArray(256)

            // Calculate the bounds of the current section
            val startX = c * sectionWidth
            val startY = r * sectionHeight
            val endX = Math.min(startX + sectionWidth, width)
            val endY = Math.min(startY + sectionHeight, height)

            // Count the total number of pixels in the current section
            val totalPixels = (endX - startX) * (endY - startY)

            // Iterate over pixels in the current section
            for (x in startX until endX) {
                for (y in startY until endY) {
                    val pixel = bitmap.getPixel(x, y)

                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)

                    redHistogram[red]++
                    greenHistogram[green]++
                    blueHistogram[blue]++
                }
            }

            // Normalize the histograms
            val redHistogramNormalized =
                redHistogram.map { it.toDouble() / totalPixels }.toTypedArray()
            val greenHistogramNormalized =
                greenHistogram.map { it.toDouble() / totalPixels }.toTypedArray()
            val blueHistogramNormalized =
                blueHistogram.map { it.toDouble() / totalPixels }.toTypedArray()

            // Store the normalized histograms of the current section
            histograms[r][c][0] = redHistogramNormalized
            histograms[r][c][1] = greenHistogramNormalized
            histograms[r][c][2] = blueHistogramNormalized
        }
    }

    return histograms
}


