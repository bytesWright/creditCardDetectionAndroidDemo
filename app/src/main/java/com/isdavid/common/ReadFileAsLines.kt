package com.isdavid.common

import android.content.Context
import java.io.IOException

fun readFileAsLines(context: Context, assetPath: String): MutableList<String> {
    return try {
        context.assets.open(assetPath).bufferedReader().use { reader ->
            reader.lineSequence().filter { it.isNotBlank() }.toMutableList()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        mutableListOf()
    }
}