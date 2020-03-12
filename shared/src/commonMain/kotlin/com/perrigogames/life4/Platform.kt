package com.perrigogames.life4

expect val isDebug: Boolean

expect fun currentTimeMillis(): Long

internal expect fun printThrowable(t: Throwable)

/**
 * Formats an integer with separators (1234567 -> 1,234,567)
 */
expect fun Int.longNumberString(): String
