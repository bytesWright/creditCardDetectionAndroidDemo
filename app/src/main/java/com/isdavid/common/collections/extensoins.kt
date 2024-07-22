package com.isdavid.common.collections


inline fun <K, V> Map<out K, V>.getOrThrow(key: K, message: String): V =
    get(key) ?: throw NullPointerException(message)


inline fun <V> List<out V>.getOrThrow(index: Int, message: String): V =
    getOrNull(index) ?: throw IndexOutOfBoundsException(message)