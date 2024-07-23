package com.isdavid.credit_card_detection.compose


import com.isdavid.R
import com.isdavid.credit_card_detection.view_model.CardSide

val CardSide.captureStepInstructions
    get() = when (this) {
        CardSide.BACK -> R.string.capturing_back
        CardSide.FRONT -> R.string.capturing_front
    }
