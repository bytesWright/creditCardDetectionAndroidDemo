package com.isdavid.handlers

import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class LatestMessageProcessor<I, O>(
    val name: String,
    private val processMessage: (input: I) -> O
) {
    private val thread = HandlerThread(name).also { it.start() }
    private val handler = Handler(thread.looper)

    private val _working = AtomicBoolean(false)
    private val _importantInput = AtomicReference<Any>(null)

    val working
        get() = _working.get()

    fun postWork(input: I, postProcess: ((result: O) -> Unit)? = null) {
        postWork(input, false, postProcess)
    }

    fun postWork(input: I, important: Boolean, postProcess: ((result: O) -> Unit)? = null) {
        if (working) {
            if (important) _importantInput.set(input)
            return
        }

        _working.set(true)

        handler.post {
            val result = processMessage(input)
            postProcess?.invoke(result)
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