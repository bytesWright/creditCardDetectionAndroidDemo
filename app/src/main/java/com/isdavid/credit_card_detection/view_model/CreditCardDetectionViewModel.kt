package com.isdavid.credit_card_detection.view_model

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import com.isdavid.common.view_model.VMF
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC.Mode
import com.isdavid.credit_card_detection.view_model.CreditCardDetectionVMC.Side
import com.isdavid.credit_card_detection.view_model.delegates.CaptureSideVMD
import com.isdavid.credit_card_detection.view_model.delegates.CardInfo
import com.isdavid.credit_card_detection.view_model.delegates.ExtractedPhrases
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMD
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMDC
import com.isdavid.credit_card_detection.view_model.delegates.SimilarityTracker
import com.isdavid.credit_card_detection.view_model.delegates.consolidateMaps
import com.isdavid.credit_card_detection.view_model.delegates.from
import com.isdavid.detection_preview.view_model.YoloVMDC
import com.isdavid.log.Logger


class CreditCardDetectionVMF(private val app: Application) : VMF<CreditCardDetectionViewModel>() {
    override fun build(): CreditCardDetectionViewModel = CreditCardDetectionViewModel(app)
}

class CreditCardDetectionViewModel(
    val app: Application,
    private val captureSideVMD: CaptureSideVMD = CaptureSideVMD(app.baseContext),
    fieldsVMD: FieldsVMD = FieldsVMD()
) :
    AndroidViewModel(app), CreditCardDetectionVMC,
    YoloVMDC by captureSideVMD, FieldsVMDC by fieldsVMD {

    private val log = Logger(
        tag = "CCD"
    )

    private val similarityTracker = SimilarityTracker()

    private val _captureMode = mutableStateOf(Mode.BOTH)
    override val captureMode: State<Mode> get() = _captureMode

    private val _capturingSide = mutableStateOf(Side.FRONT)
    override val capturingSide: State<Side> get() = _capturingSide

    init {
        captureSideVMD.onSideDetected = ::onSideDetected
    }

    var stopCamera: (() -> Unit) = {}

    fun setCaptureMode(mode: Mode, side: Side = Side.FRONT) {
        similarityTracker.reset()
        this._captureMode.value = mode
        this._capturingSide.value = side
    }

    private var consolidatedData: ExtractedPhrases = mapOf()

    private fun onSideDetected(detectedBitmap: Bitmap, extractedValues: ExtractedPhrases) {
        val detected = detectedBitmap.asImageBitmap()

        val isSimilar = similarityTracker.isSimilarToPreviousDetection(app.baseContext, detectedBitmap)
        log.line { "Capturing ${capturingSide.value} isSimilar $isSimilar" }

        when(capturingSide.value){
            Side.BACK -> setCandidateBackImage(detected)
            Side.FRONT -> setCandidateFrontImage(detected)
        }

        if (isSimilar) {
            return
        }

        consolidatedData = consolidateMaps(
            consolidatedData,
            extractedValues
        )

        checkIfProcessCompleted()
        storeImageAndPrepareForNext(detected)
    }

    private fun storeImageAndPrepareForNext(detected: ImageBitmap) {
        when (capturingSide.value) {
            Side.BACK -> {
                log.line { "Captured back" }
                setBackImage(detected)
            }

            Side.FRONT -> {
                log.line { "Captured front" }
                setFrontImage(detected)
                _capturingSide.value = Side.BACK
            }
        }
    }

    private fun checkIfProcessCompleted() {
        if (
            captureMode.value == Mode.BOTH && _capturingSide.value == Side.BACK ||
            captureMode.value == Mode.SINGLE
        ) {
            val values = CardInfo.from(consolidatedData)

            val newCreditCardNumber = values.creditCardNumber.getOrNull(0)
            if (newCreditCardNumber != null) setCreditCardNumber(newCreditCardNumber)

            val date = values.date.getOrNull(0)
            if (date != null) setDate(date)

            val securityNumber = values.securityNumber.getOrNull(0)
            if (securityNumber != null) setSecurityNumber(securityNumber)

            stopCamera()
        }
    }

    override fun onCleared() {
        super.onCleared()
        captureSideVMD.clear()
    }
}

