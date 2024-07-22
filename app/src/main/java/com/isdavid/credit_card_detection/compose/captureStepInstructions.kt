package com.isdavid.credit_card_detection.compose


import com.isdavid.R
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC.Side

val Side.captureStepInstructions
    get() = when (this) {
        Side.BACK -> R.string.capturing_back
        Side.FRONT -> R.string.capturing_front
    }
