package com.isdavid

import com.isdavid.credit_card_detection.view_model.delegates.extractData
import org.junit.Test

class ExampleUnitInternalActivity {
    @Test
    fun testCreditCardNumberExtraction() {
        val inputString = "here 1234 5678 1234 5678 01-21 123"
        val result = extractData(inputString)

        result.forEach { (label, matches) ->
            println("$label: $matches")
        }
    }
}

