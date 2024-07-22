package com.isdavid.credit_card_detection.view_model.delegates

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar

fun bitmapToMat(bitmap: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)
    return mat
}

fun bitmapToMat32FC4(bitmap: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)

    val mat32FC4 = Mat(mat.size(), CvType.CV_32FC4)
    mat.convertTo(mat32FC4, CvType.CV_32FC4, 1.0 / 255.0)

    return mat32FC4
}

fun subtractBitmaps(mat1: Mat, mat2: Mat): Scalar {
    val resultMat = Mat()
    Core.subtract(mat2, mat1, resultMat)
    return Core.mean(resultMat)
}


fun blurAndScale(
    context: Context,
    image: Bitmap,
    width: Int = 500,
    height: Int = 500,
    blur: Float = 7.5f
): Bitmap {
    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap =
        inputBitmap.copy(Bitmap.Config.ARGB_8888, true) // Necessary for RenderScript

    val renderScript = RenderScript.create(context)
    val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

    val tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)

    blurScript.setRadius(blur)
    blurScript.setInput(tmpIn)
    blurScript.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    renderScript.destroy()
    return outputBitmap
}
