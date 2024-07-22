package com.isdavid.common.handlers

import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class LatestMessageProcessor<I>(
    val name: String,
    private val processMessage: (input: I) -> Unit
) {
    private val thread = HandlerThread(name).also { it.start() }
    private val handler = Handler(thread.looper)

    private val _working = AtomicBoolean(false)
    private val _importantInput = AtomicReference<Any>(null)

    val working
        get() = _working.get()

    fun postWork(input: I, important: Boolean = false) {
        if (working) {
            if (important) _importantInput.set(input)
            return
        }

        _working.set(true)

        handler.post {
            processMessage(input)
            _working.set(false)
        }
    }

    fun quitSafely() {
        thread.quitSafely()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}