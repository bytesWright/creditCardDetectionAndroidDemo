package com.isdavid.machine_vision.ocr

import android.graphics.Bitmap
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrWrapper {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun run(image: Bitmap): Text {
        val inputImage = InputImage.fromBitmap(image, 0)
        return Tasks.await(recognizer.process(inputImage))
    }
}