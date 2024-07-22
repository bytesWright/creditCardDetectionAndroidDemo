package com.isdavid.machine_vision.image.histogram

import kotlin.math.pow
import kotlin.math.sqrt


fun <T : Number> compareHistograms(
    hist1: Array<Array<T>>,
    hist2: Array<Array<T>>
): Map<String, Double> {
    val results = mutableMapOf<String, Double>()
    results["Correlation"] = correlation(hist1, hist2)
    results["ChiSquare"] = chiSquare(hist1, hist2)
    results["Intersection"] = intersection(hist1, hist2)
    results["Bhattacharyya"] = bhattacharyya(hist1, hist2)
    return results
}

/**
 * Calculates the correlation between two histograms.
 *
 * This function computes the correlation coefficient between two histograms.
 * The histograms are represented as 2D arrays where each sub-array corresponds to a color channel (red, green, blue).
 * The correlation coefficient is a measure of the linear relationship between two datasets. It ranges from -1 to 1,
 * where 1 indicates a perfect positive linear relationship, -1 indicates a perfect negative linear relationship,
 * and 0 indicates no linear relationship.
 *
 * @param hist1 The first histogram represented as an array of three arrays, each containing the histogram values for
 *              the red, green, and blue channels.
 * @param hist2 The second histogram represented as an array of three arrays, each containing the histogram values for
 *              the red, green, and blue channels.
 * @return The correlation coefficient between the two histograms. If the denominator is zero, the function returns 0.0.
 * @throws IllegalArgumentException If the input histograms do not have the same dimensions.
 */
fun <T : Number> correlation(hist1: Array<Array<T>>, hist2: Array<Array<T>>): Double {
    val n = hist1[0].size
    var sum1 = 0.0
    var sum2 = 0.0
    var sum1Sq = 0.0
    var sum2Sq = 0.0
    var pSum = 0.0

    for (i in 0 until n) {
        val s1 = hist1[0][i].toDouble() + hist1[1][i].toDouble() + hist1[2][i].toDouble()
        val s2 = hist2[0][i].toDouble() + hist2[1][i].toDouble() + hist2[2][i].toDouble()

        sum1 += s1
        sum2 += s2

        sum1Sq += s1.pow(2)
        sum2Sq += s2.pow(2)

        pSum += s1 * s1
    }

    val num = pSum - (sum1 * sum2 / n)
    val den = sqrt((sum1Sq - sum1.pow(2) / n) * (sum2Sq - sum2.pow(2) / n))

    return if (den == 0.0) 0.0 else num / den
}

/**
 * Calculates the Chi-Square distance between two histograms.
 *
 * This function computes the Chi-Square distance between two histograms.
 * The histograms are represented as 2D arrays where each sub-array corresponds to a color channel (red, green, blue).
 * The Chi-Square distance is a measure of the difference between two histograms. Lower values indicate more similar histograms.
 *
 * The formula used for each bin is:
 *      ( (hist1[i] - hist2[i])^2 ) / (hist1[i] + hist2[i] + 1)
 * The +1 in the denominator is used to avoid division by zero.
 *
 * @param hist1 The first histogram represented as an array of three arrays, each containing the histogram values for
 *              the red, green, and blue channels.
 * @param hist2 The second histogram represented as an array of three arrays, each containing the histogram values for
 *              the red, green, and blue channels.
 * @return The Chi-Square distance between the two histograms. A lower value indicates more similar histograms.
 * @throws IllegalArgumentException If the input histograms do not have the same dimensions.
 */
fun <T : Number> chiSquare(hist1: Array<Array<T>>, hist2: Array<Array<T>>): Double {
    var sum = 0.0
    for (i in hist1[0].indices) {
        val diffR = hist1[0][i].toDouble() - hist2[0][i].toDouble()
        val diffG = hist1[1][i].toDouble() - hist2[1][i].toDouble()
        val diffB = hist1[2][i].toDouble() - hist2[2][i].toDouble()
        sum += (diffR * diffR) / (hist1[0][i].toDouble() + hist2[0][i].toDouble() + 1.0)
        sum += (diffG * diffG) / (hist1[1][i].toDouble() + hist2[1][i].toDouble() + 1.0)
        sum += (diffB * diffB) / (hist1[2][i].toDouble() + hist2[2][i].toDouble() + 1.0)
    }
    return sum
}

fun <T : Number> intersection(hist1: Array<Array<T>>, hist2: Array<Array<T>>): Double {
    var sum = 0.0
    for (i in hist1[0].indices) {
        sum += minOf(hist1[0][i].toDouble(), hist2[0][i].toDouble())
        sum += minOf(hist1[1][i].toDouble(), hist2[1][i].toDouble())
        sum += minOf(hist1[2][i].toDouble(), hist2[2][i].toDouble())
    }
    return sum
}

fun <T : Number> bhattacharyya(hist1: Array<Array<T>>, hist2: Array<Array<T>>): Double {
    var sum = 0.0
    for (i in hist1[0].indices) {
        sum += sqrt((hist1[0][i].toDouble() * hist2[0][i].toDouble()))
        sum += sqrt((hist1[1][i].toDouble() * hist2[1][i].toDouble()))
        sum += sqrt((hist1[2][i].toDouble() * hist2[2][i].toDouble()))
    }
    return -Math.log(sum)
}
