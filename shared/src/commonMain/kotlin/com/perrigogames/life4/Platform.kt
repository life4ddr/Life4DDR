package com.perrigogames.life4

import com.russhwolf.settings.Settings

expect val isDebug: Boolean

fun Settings.getDebugBoolean(key: String) = isDebug && getBoolean(key, false)

expect fun currentTimeMillis(): Long

internal expect fun log(key: String, message: String)

internal expect fun printThrowable(t: Throwable)

/**
 * Formats an integer with separators (1234567 -> 1,234,567)
 */
expect fun Int.longNumberString(): String
