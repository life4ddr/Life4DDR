package com.perrigogames.life4

import java.text.DecimalFormat

actual val isDebug: Boolean get() = false

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun log(key: String, message: String) {
    print("($key): $message")
}

actual fun logE(key: String, message: String) {
    print("($key) ERROR: $message")
}

actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun logException(t: Throwable) {}
actual fun setCrashInt(key: String, v: Int) {}
actual fun setCrashString(key: String, v: String) {}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
