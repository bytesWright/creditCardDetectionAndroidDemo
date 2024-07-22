package com.isdavid.credit_card_detection.view_model.delegates

typealias ExtractedPhrases = Map<String, List<String>>

interface CardInfoC {
    val creditCardNumber: List<String>
    val date: List<String>
    val securityNumber: List<String>
}

data class CardInfo(
    override val creditCardNumber: List<String> = emptyList(),
    override val date: List<String> = emptyList(),
    override val securityNumber: List<String> = emptyList()
) : CardInfoC {
    companion object
}

fun CardInfo.Companion.from(data: ExtractedPhrases) = CardInfo(
    data.getOrDefault(PatternType.CREDIT_CARD_NUMBER.label, emptyList()),
    data.getOrDefault(PatternType.DATE.label, emptyList()),
    data.getOrDefault(PatternType.SECURITY_NUMBER.label, emptyList())
)
