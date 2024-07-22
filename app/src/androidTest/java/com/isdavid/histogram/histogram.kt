package com.isdavid.histogram

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isdavid.log.buildQuickLogger
import com.isdavid.machine_vision.image.compareSectionedHistograms
import com.isdavid.machine_vision.image.histogram.calculateSectionedHistogram
import com.isdavid.machine_vision.image.histogram.calculateSectionedHistogramNormalized
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SingleDetectionTest {
    private val log = buildQuickLogger("HST") //histogram


    @Test
    fun tesCalculateSectionedHistogram() {
        val image = createColoredBitmap(20, 20)
        val sectionedHistogram = calculateSectionedHistogram(image, 2, 2)

        assert(sectionedHistogram[0][0][0][255] == 100)
        assert(sectionedHistogram[0][1][1][255] == 100)
        assert(sectionedHistogram[1][0][2][255] == 100)

        assert(sectionedHistogram[1][1][0][255] == 100)
        assert(sectionedHistogram[1][1][1][255] == 100)
        assert(sectionedHistogram[1][1][2][255] == 100)

        image.recycle()
    }

    @Test
    fun tesComp() {
        val image = createColoredBitmap(20, 20)
        val image2 = createColoredBitmap(40, 40)

        val h1 = calculateSectionedHistogramNormalized(image, 5, 5)
        val h2 = calculateSectionedHistogramNormalized(image2, 5, 5)

        val result = compareSectionedHistograms(h1, h2, 5, 5, .9)
        log(">> $result")
    }

}


fun createColoredBitmap(width: Int, height: Int): Bitmap {
    // Create a blank bitmap with the specified width and height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Create a canvas to draw on the bitmap
    val canvas = Canvas(bitmap)

    // Calculate the width and height of each sector
    val sectorWidth = width / 2
    val sectorHeight = height / 2

    // Create paint objects for each color
    val redPaint = Paint().apply { color = Color.rgb(255, 0, 0) }
    val bluePaint = Paint().apply { color = Color.rgb(0, 255, 0) }
    val greenPaint = Paint().apply { color = Color.rgb(0, 0, 255) }
    val whitePaint = Paint().apply { color = Color.rgb(255, 255, 255) }

    // Draw each sector with the respective color
    canvas.drawRect(
        0f,
        0f,
        sectorWidth.toFloat(),
        sectorHeight.toFloat(),
        redPaint
    )      // Sector 0,0
    canvas.drawRect(
        sectorWidth.toFloat(),
        0f,
        width.toFloat(),
        sectorHeight.toFloat(),
        bluePaint
    )  // Sector 0,1
    canvas.drawRect(
        0f,
        sectorHeight.toFloat(),
        sectorWidth.toFloat(),
        height.toFloat(),
        greenPaint
    ) // Sector 1,0
    canvas.drawRect(
        sectorWidth.toFloat(),
        sectorHeight.toFloat(),
        width.toFloat(),
        height.toFloat(),
        whitePaint
    ) // Sector 1,1

    return bitmap
}
