package com.isdavid.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue


@RunWith(AndroidJUnit4::class)
class OcrTest {
    private lateinit var recognizer: TextRecognizer
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        recognizer = TextRecognition.getClient(
            TextRecognizerOptions
                .DEFAULT_OPTIONS
        )
    }

    @Test
    fun ocrTest() {
        val inputStream: InputStream = context.assets.open("testAssets/t0.png")
        val testBitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        val inputImage = InputImage.fromBitmap(testBitmap, 0)

        val (result, time) = measureTimedValue {
            val task: Task<Text> = recognizer.process(inputImage)
            runBlocking {
                Tasks.await(task)
            }
        }

        Log.d("DXXD", "text blocks ${result.textBlocks.size} time ${time.toDouble(DurationUnit.SECONDS)}")
    }
}

