package com.isdavid.common.handlers

import android.os.Handler
import android.os.HandlerThread

class LoopHandlerWrapper(name: String) {
    private val thread = HandlerThread(name).also { it.start() }
    val handler = Handler(thread.looper)
    fun quitSafely() {
        thread.quitSafely()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}