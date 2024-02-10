package com.perrigogames.life4

import android.util.Log
import java.text.DecimalFormat

actual val isDebug: Boolean get() = false // FIXME

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun logE(key: String, message: String) {
    Log.e(key, message)
}

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

internal actual fun logException(t: Throwable) {
}

internal actual fun setCrashInt(key: String, v: Int) {
}

internal actual fun setCrashString(key: String, v: String) {
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
