package com.isdavid.log

import android.util.Log

@Suppress("MemberVisibilityCanBePrivate")
class Logger(
    private val tag: String,
    var talk: Boolean = true,
    var levelLimit: Int = Int.MAX_VALUE
) {
    private val countLog = mutableMapOf<String, Int>()

    fun line(
        level: Int = 0,
        label: String? = null,
        limit: Int = -1,
        until: (() -> Boolean)? = null,
        tag: String? = null,
        message: String
    ) {
        if (!talk || level > levelLimit) return
        if (until != null && !until()) return

        if (limit > 0 && label != null) {
            val count = countLog.getOrDefault(label, 0)
            if (count >= limit) return
            countLog[label] = count + 1
        }

        Log.d(tag?: this.tag, message)
    }

    fun block(
        level: Int = 0,
        label: String? = null,
        limit: Int = -1,
        until: (() -> Boolean)? = null,
        tag: String? = null,
        messages: List<String>
    ) {
        if (!talk || level > levelLimit) return
        if (until != null && !until()) return

        if (limit > 0 && label != null) {
            val count = countLog.getOrDefault(label, 0)
            if (count >= limit) return
            countLog[label] = count + 1
        }

        val finalTag = tag?: this.tag

        messages.forEach {
            Log.d(finalTag, it)
        }
    }

    fun line(
        level: Int = 0,
        label: String? = null,
        limit: Int = -1,
        until: (() -> Boolean)? = null,
        tag: String? = null,
        message: () -> String
    ) = line(level, label, limit, until, tag, message())

    fun block(
        level: Int = 0,
        label: String? = null,
        limit: Int = -1,
        until: (() -> Boolean)? = null,
        tag: String? = null,
        messages: () -> List<String>
    ) = block(level, label, limit, until,tag, messages())

    companion object {
        private val loggersByTag = mutableMapOf<String, Logger>()

        fun provide(tag: String, talk: Boolean = true, levelLimit: Int = Int.MAX_VALUE): Logger {
            val logger = loggersByTag.computeIfAbsent(tag) {
                Logger(it)
            }

            logger.talk = talk
            logger.levelLimit = levelLimit

            return logger
        }
    }
}