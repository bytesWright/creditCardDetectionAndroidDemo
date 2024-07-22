package com.isdavid.log

import android.util.Log

fun buildQuickLogger(
    tag: String,
    talk: Boolean = true,
): (message: String) -> Unit {
    return if (talk) { message: String ->
        Log.d(tag, message)
    } else {
        { _: String -> }
    }
}


