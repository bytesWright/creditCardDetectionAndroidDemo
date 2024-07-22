package com.isdavid.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isdavid.credit_card_detection.view_model.delegates.bitmapToMat
import com.isdavid.credit_card_detection.view_model.delegates.bitmapToMat32FC4
import com.isdavid.credit_card_detection.view_model.delegates.blurAndScale
import com.isdavid.log.buildQuickLogger
import com.isdavid.machine_vision.open_cv.flatten
import com.isdavid.machine_vision.open_cv.isSimilar
import com.isdavid.machine_vision.open_cv.mse
import com.isdavid.machine_vision.open_cv.split
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import java.io.InputStream
import java.util.Collections.max


@RunWith(AndroidJUnit4::class)
class Opencv {
    private val log = buildQuickLogger("MDT") //model detection test

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");
    }

    @Test
    fun opencvMatTest() {
        val m = create4x4MatCV_8SC3(210.0, 50.0, 60.0)
        val m2 = create4x4MatCV_8SC3(220.0, 205.0, 214.0)

        Log.d("DIFF", "M1 ${m.toStr()}")
        Log.d("DIFF", "M2 ${m2.toStr()}")

        val resultMat = Mat()
        Core.subtract(m, m2, resultMat)
        val mean: Scalar = Core.mean(resultMat)

        Log.d("DIFF", "RM ${resultMat.toStr()}")
        Log.d("DIFF", "mean ${mean.toArray().joinToString { "$it" }}")

        Log.d("DIFF", "-------")

        val resultMat2 = Mat()
        Core.subtract(m2, m, resultMat2)
        val mean2: Scalar = Core.mean(resultMat2)

        Log.d("DIFF", "RM ${resultMat2.toStr()}")
        Log.d("DIFF", "mean ${mean2.toArray().joinToString { "$it" }}")
    }

    @Test
    fun mse32FC4Test() {
        val a = loadImage(context, "testAssets/similarity/a.png")
        val b = loadImage(context, "testAssets/similarity/b.png")

        val blurredA = bitmapToMat32FC4(blurAndScale(context, a))
        val blurredB = bitmapToMat32FC4(blurAndScale(context, b))

        Log.d("DIFF", "RM ${blurredA.mse(blurredB)}")
        Log.d("DIFF", "RM ${blurredB.mse(blurredA)}")
    }

    @Test
    fun isSimilar32FC4Test() {
        val a = loadImage(context, "testAssets/similarity/a.png")
        val b = loadImage(context, "testAssets/similarity/b.png")

        val blurredA = bitmapToMat32FC4(blurAndScale(context, a))
        val blurredB = bitmapToMat32FC4(blurAndScale(context, b))

        val sA = blurredA.split(5, 5)
        val sB = blurredB.split(5, 5)

        Log.d("DIFF", "RM ${sA.isSimilar(sB, .2)}")
        Log.d("DIFF", "RM ${sA.mse(sB)}")

        Log.d("DIFF", "RM ${sB.isSimilar(sA, .2)}")
        Log.d("DIFF", "RM ${sB.mse(sA)}")
    }

    @Test
    fun isSimilar32FC4TestVerySimilar2() {
        val a = loadImage(context, "testAssets/similarity/vsa.png")
        val b = loadImage(context, "testAssets/similarity/vsb.png")

        val blurredA = bitmapToMat32FC4(blurAndScale(context, a))
        val blurredB = bitmapToMat32FC4(blurAndScale(context, b))

        val sA = blurredA.split(8, 8)
        val sB = blurredB.split(8, 8)

        Log.d("DIFF", "isSimilar ${sB.isSimilar(sA, .15)}")

        val mse = sB.mse(sA).flatten()
        Log.d("DIFF", "mse ${mse.joinToString { "$it" }}")
        Log.d("DIFF", "max mse ${max(mse)}")
        Log.d("DIFF", "above threshold ${mse.filter { it > .15 }.joinToString { "$it" }}")
        Log.d("DIFF", "above threshold count ${mse.filter { it > .15 }.size}")
    }

    @Test
    fun isSimilar32FC4TestVerySimilar() {
        val a = loadImage(context, "testAssets/similarity/vsa.png")
        val b = loadImage(context, "testAssets/similarity/vsc.png")

        val blurredA = bitmapToMat32FC4(blurAndScale(context, a))
        val blurredB = bitmapToMat32FC4(blurAndScale(context, b))

        val sA = blurredA.split(5, 5)
        val sB = blurredB.split(5, 5)

        Log.d("DIFF", "RM ${sA.isSimilar(sB, .15)}")
        Log.d("DIFF", "RM ${sA.mse(sB)}")
    }

    @Test
    fun mseTest() {
        val a = loadImage(context, "testAssets/similarity/a.png")
        val b = loadImage(context, "testAssets/similarity/b.png")

        val blurredA = bitmapToMat(blurAndScale(context, a))
        val blurredB = bitmapToMat(blurAndScale(context, b))

        Log.d("DIFF", "RM ${blurredA.mse(blurredB)}")
        Log.d("DIFF", "RM ${blurredA.mse(blurredB)}")
        Log.d("DIFF", "RM ${blurredB.mse(blurredA)}")
    }
}


fun loadImage(context: Context, path: String): Bitmap {
    val inputStream: InputStream = context.assets.open(path)
    return BitmapFactory.decodeStream(inputStream)
}


fun Scalar.toArray() = `val`
fun Scalar.toStr() = `val`.joinToString { "$it" }


fun create4x4MatCV_8SC3(a: Double, b: Double, c: Double): Mat {
    // Create a 4x4 Mat of type CV_8SC3 (8-bit signed three-channel) initialized with zeros
    val mat = Mat(2, 2, CvType.CV_32FC3, Scalar(a, b, c))
    return mat
}


fun Mat.toFloatArray(): FloatArray {
    val size = total().toInt() * channels()
    val floatArray = FloatArray(size)
    get(0, 0, floatArray)
    return floatArray
}


fun Mat.toStr() = toFloatArray().joinToString { "$it" }

