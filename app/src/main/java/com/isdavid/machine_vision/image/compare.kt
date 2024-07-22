package com.isdavid.machine_vision.image

import android.util.Log
import com.isdavid.log.strRound
import com.isdavid.machine_vision.image.histogram.SectionedHistogram
import com.isdavid.machine_vision.image.histogram.chiSquare
import com.isdavid.machine_vision.image.histogram.correlation

/**
 * Compares the sectioned histograms of two bitmaps using the correlation function.
 *
 * This function calculates the sectioned histograms for two bitmaps and then compares each corresponding section
 * using the correlation coefficient. If the average correlation coefficient across all sections is above the
 * specified threshold, the function returns true indicating the images are very similar.
 *
 * @param bitmapA The first bitmap to be compared.
 * @param histogramB The second bitmap to be compared.
 * @param rows The number of rows to divide the bitmaps into for histogram calculation. Default is 10.
 * @param columns The number of columns to divide the bitmaps into for histogram calculation. Default is 10.
 * @param threshold The threshold for the average correlation coefficient to consider the images as very similar.
 *                  Default is 0.8.
 *
 * @return True if the images are very similar, otherwise false.
 */
fun compareSectionedHistograms(
    histogramA: SectionedHistogram<Double>,
    histogramB: SectionedHistogram<Double>,
    rows: Int,
    columns: Int,
    threshold: Double
): Boolean {
    var totalCorrelation = 0.0
    var count = 0

    Log.d("SMT", "--------")

    for (r in 0 until rows) {
        for (c in 0 until columns) {
            val sectionHistA = histogramA[r][c]
            val sectionHistB = histogramB[r][c]

            val correlationValue = correlation(sectionHistA, sectionHistB)

            Log.d("SMT", "${correlationValue < threshold} $r $c   ${threshold.strRound(5)}    ${correlationValue.strRound(5)}")

            if (correlationValue < threshold) {
                return false
            }

            totalCorrelation += correlationValue
            count++
        }
    }

    return true
//    val averageCorrelation = totalCorrelation / count
//    if(averageCorrelation < threshold) Log.d("DXXD", "GF $averageCorrelation $threshold")
//    return averageCorrelation >= threshold
}