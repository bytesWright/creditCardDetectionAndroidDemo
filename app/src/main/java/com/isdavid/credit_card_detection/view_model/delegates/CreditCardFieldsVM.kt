package com.isdavid.credit_card_detection.view_model.delegates

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap

interface FieldsVMDC {
    val frontImage: State<ImageBitmap>
    val backImage: State<ImageBitmap>
    val candidateFrontImage: State<ImageBitmap>
    val candidateBackImage: State<ImageBitmap>

    val cardholderName: State<String>
    val creditCardNumberOptions: State<List<String>>
    val dateOptions: State<List<String>>
    val securityNumberOptions: State<List<String>>

    val creditCardNumber: State<String>
    val date: State<String>
    val securityNumber: State<String>

    fun setCardholderName(name: String)
    fun setFrontImage(image: ImageBitmap)
    fun setBackImage(image: ImageBitmap)
    fun setCreditCardNumberOptions(options: List<String>)
    fun setDateOptions(options: List<String>)
    fun setSecurityNumberOptions(options: List<String>)
    fun setCreditCardNumber(number: String)
    fun setDate(date: String)
    fun setSecurityNumber(number: String)
    fun setCandidateFrontImage(image: ImageBitmap)
    fun setCandidateBackImage(image: ImageBitmap)
}

class FieldsVMD : FieldsVMDC {
    private val pFrontImage = mutableStateOf(emptyImageBitmap())
    override val frontImage: State<ImageBitmap> get() = pFrontImage

    private val pBackImage = mutableStateOf(emptyImageBitmap())
    override val backImage: State<ImageBitmap> get() = pBackImage

    private val pCandidateFrontImage = mutableStateOf(emptyImageBitmap())
    override val candidateFrontImage: State<ImageBitmap> get() = pCandidateFrontImage

    private val pCandidateBackImage = mutableStateOf(emptyImageBitmap())
    override val candidateBackImage: State<ImageBitmap> get() = pCandidateBackImage

    private val pCreditCardNumberOptions = mutableStateOf(emptyList<String>())
    override val creditCardNumberOptions: State<List<String>> get() = pCreditCardNumberOptions

    private val pDateOptions = mutableStateOf(emptyList<String>())
    override val dateOptions: State<List<String>> get() = pDateOptions

    private val pSecurityNumberOptions = mutableStateOf(emptyList<String>())
    override val securityNumberOptions: State<List<String>> get() = pSecurityNumberOptions

    private val pCreditCardNumber = mutableStateOf("")
    override val creditCardNumber: State<String> get() = pCreditCardNumber

    private val pDate = mutableStateOf("")
    override val date: State<String> get() = pDate

    private val pSecurityNumber = mutableStateOf("")
    override val securityNumber: State<String> get() = pSecurityNumber

    private val pCardholderName = mutableStateOf("")
    override val cardholderName: State<String> get() = pCardholderName


    override fun setCardholderName(name: String) {
        pCardholderName.value = name
    }

    override fun setFrontImage(image: ImageBitmap) {
        pFrontImage.value = image
    }

    override fun setBackImage(image: ImageBitmap) {
        pBackImage.value = image
    }

    override fun setCandidateFrontImage(image: ImageBitmap) {
        pCandidateFrontImage.value = image
    }

    override fun setCandidateBackImage(image: ImageBitmap) {
        pCandidateBackImage.value = image
    }

    override fun setCreditCardNumberOptions(options: List<String>) {
        pCreditCardNumberOptions.value = options
    }

    override fun setDateOptions(options: List<String>) {
        pDateOptions.value = options
    }

    override fun setSecurityNumberOptions(options: List<String>) {
        pSecurityNumberOptions.value = options
    }

    override fun setCreditCardNumber(number: String) {
        pCreditCardNumber.value = number
    }

    override fun setDate(date: String) {
        pDate.value = date
    }

    override fun setSecurityNumber(number: String) {
        pSecurityNumber.value = number
    }
}
