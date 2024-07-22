package com.isdavid.log

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.msString(decimals: Int = 3): String {
    return toString(DurationUnit.MILLISECONDS, decimals)
}

fun Double.strRound(decimals: Int = 3): String {
    return "%.${decimals}f".format(this)
}