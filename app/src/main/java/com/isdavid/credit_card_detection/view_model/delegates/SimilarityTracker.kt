package com.isdavid.credit_card_detection.view_model.delegates

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.isdavid.machine_vision.open_cv.compareTo
import com.isdavid.machine_vision.open_cv.mse
import com.isdavid.machine_vision.open_cv.split
import org.opencv.core.Mat
import org.opencv.core.Scalar


class SimilarityTracker {
    private var previousDetected: List<Mat>? = null
    fun reset() {
        Log.d("SMT", "SimilarityTracker.reset")
        previousDetected = null
    }

    @Synchronized
    fun isSimilarToPreviousDetection(
        context: Context,
        detectedBitmap: Bitmap,
        threshold: Double = .22
    ): Boolean {
        val blurredImage = blurAndScale(
            context,
            detectedBitmap,
            500, 500
        )

        val currentBlurred = bitmapToMat32FC4(blurredImage)
        val current = currentBlurred.split(6, 6)
        val previousDetected = previousDetected

        if (previousDetected == null) {
            this.previousDetected = current
            Log.d("CCD", "isSimilarToPreviousDetection default false previous is null")
            return false
        }

        val mse: List<Scalar> = previousDetected.mse(current)
        val isSimilar = mse.find { it > threshold } == null

        Log.d(
            "CCD",
            "isSimilarToPreviousDetection $isSimilar $mse"
        )

        return isSimilar
    }
}



