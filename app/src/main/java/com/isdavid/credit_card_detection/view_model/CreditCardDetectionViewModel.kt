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
import com.isdavid.credit_card_detection.view_model.delegates.CaptureSideVMD
import com.isdavid.credit_card_detection.view_model.delegates.CardInfo
import com.isdavid.credit_card_detection.view_model.delegates.ExtractedPhrases
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMD
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMDC
import com.isdavid.credit_card_detection.view_model.delegates.consolidateMaps
import com.isdavid.credit_card_detection.view_model.delegates.from
import com.isdavid.detection_preview.view_model.YoloVMDC
import com.isdavid.log.Logger


class CreditCardDetectionVMF(private val app: Application) : VMF<CreditCardDetectionViewModel>() {
    override fun build(): CreditCardDetectionViewModel = CreditCardDetectionViewModel(app)
}

class CreditCardDetectionViewModel(
    val app: Application, private val captureSideVMD: CaptureSideVMD = CaptureSideVMD(app.baseContext), fieldsVMD: FieldsVMD = FieldsVMD()
) : AndroidViewModel(app), CreditCardDetectionVMC, YoloVMDC by captureSideVMD, FieldsVMDC by fieldsVMD {

    private val log = Logger(
        tag = "CCD"
    )

    private val _captureMode = mutableStateOf(Mode.BOTH)
    override val captureMode: State<Mode> get() = _captureMode

    private val _capturingCardSide = mutableStateOf(CardSide.FRONT)
    override val capturingCardSide: State<CardSide> get() = _capturingCardSide

    private var consolidatedData: ExtractedPhrases = mapOf()
    private var capturedSides: MutableSet<CardSide> = mutableSetOf()

    init {
        captureSideVMD.onSideDetected = ::onSideDetected
    }

    var stopCamera: (() -> Unit) = {}

    fun setCaptureMode(mode: Mode, cardSide: CardSide = CardSide.FRONT) {
        this._captureMode.value = mode
        this._capturingCardSide.value = cardSide
    }

    fun clearValues() {
        consolidatedData = mapOf()
        capturedSides = mutableSetOf()
    }

    private fun onSideDetected(detectedBitmap: Bitmap, cardSide: CardSide, extractedValues: ExtractedPhrases) {
        val detected = detectedBitmap.asImageBitmap()

        when (cardSide) {
            CardSide.BACK -> setCandidateBackImage(detected)
            CardSide.FRONT -> setCandidateFrontImage(detected)
        }

        consolidatedData = consolidateMaps(
            consolidatedData, extractedValues
        )

        checkIfProcessCompleted()
        storeImageAndPrepareForNext(cardSide, detected)
    }

    private fun storeImageAndPrepareForNext(cardSide: CardSide, detected: ImageBitmap) {
        capturedSides.add(cardSide)

        when (cardSide) {
            CardSide.BACK -> {
                log.line { "Captured back" }
                setBackImage(detected)
                _capturingCardSide.value = CardSide.FRONT
            }

            CardSide.FRONT -> {
                log.line { "Captured front" }
                setFrontImage(detected)
                _capturingCardSide.value = CardSide.BACK
            }
        }
    }

    private fun checkIfProcessCompleted() {
        val formIsFilled = fillForm()
        val capturedBothSides = capturedSides.size == 2

        if (formIsFilled || capturedBothSides || captureMode.value == Mode.SINGLE) {
            stopCamera()
        }
    }

    private fun fillForm(): Boolean {
        val values = CardInfo.from(consolidatedData)

        val newCreditCardNumber = values.creditCardNumber.getOrNull(0)
        if (newCreditCardNumber != null) setCreditCardNumber(newCreditCardNumber)

        val date = values.date.getOrNull(0)
        if (date != null) setDate(date)

        val securityNumber = values.securityNumber.getOrNull(0)
        if (securityNumber != null) setSecurityNumber(securityNumber)

        val success = null !in arrayOf(securityNumber, date, newCreditCardNumber)
        return success
    }

    override fun onCleared() {
        super.onCleared()
        captureSideVMD.clear()
    }
}

