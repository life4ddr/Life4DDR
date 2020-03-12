package com.perrigogames.life4

import java.text.DecimalFormat

actual val isDebug: Boolean get() = BuildConfig.DEBUG

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
