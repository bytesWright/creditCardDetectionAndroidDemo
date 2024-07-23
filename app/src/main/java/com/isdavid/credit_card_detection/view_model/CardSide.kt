package com.isdavid.credit_card_detection.view_model

enum class CardSide(val classId: Int) {
    FRONT(0), BACK(1);

    companion object {
        fun build(classId: Int): CardSide {
            return when (classId) {
                0 -> FRONT
                1 -> BACK
                else -> throw Exception("Invalid class id")
            }
        }
    }
}