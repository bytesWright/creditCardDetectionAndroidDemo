package com.isdavid.model

import android.content.Context
import android.util.Log
import android.view.textclassifier.TextClassification
import android.view.textclassifier.TextClassificationManager
import android.view.textclassifier.TextClassifier
import androidx.core.content.ContextCompat.getSystemService
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.mlkit.vision.text.TextRecognizer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TextClassificationTest {
    private lateinit var recognizer: TextRecognizer
    private lateinit var context: Context

    private lateinit var textClassifier: TextClassifier

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        val tcm = getSystemService(
            context,
            TextClassificationManager::class.java
        ) as TextClassificationManager

        textClassifier = tcm.textClassifier
    }

    private fun classifyText(text: String): TextClassification {
        val charSequence: CharSequence = text

        val request = TextClassification
            .Request
            .Builder(charSequence, 0, text.length)
            .build()

        return textClassifier.classifyText(request)
    }

    private fun displayClassificationResults(classification: TextClassification) {
        val result = StringBuilder()

        for (i in 0 until classification.entityCount) {
            val entityType = classification.getEntity(i)
            val confidenceScore = classification.getConfidenceScore(entityType)
            val text = classification.text
            result.append("$text, Entity: $entityType, Confidence: $confidenceScore\n")
        }

        Log.d("DXXD", result.toString())


    }

    @Test
    fun testClassifier() {
        Log.d("DXXD", ">>>>")
        val result = classifyText("James Smith")
        displayClassificationResults(result)
    }
}

