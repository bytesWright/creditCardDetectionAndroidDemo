package com.isdavid.credit_card_detection.view_model

import androidx.compose.runtime.State
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMDC
import com.isdavid.detection_preview.view_model.YoloVMDC

interface CreditCardDetectionVMC : YoloVMDC, FieldsVMDC {
    val capturingSide: State<Side>
    val captureMode: State<Mode>

    enum class Side {
        BACK,
        FRONT
    }

    enum class Mode {
        BOTH,
        SINGLE
    }
}