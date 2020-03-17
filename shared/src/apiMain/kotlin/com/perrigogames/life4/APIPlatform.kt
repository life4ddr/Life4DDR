package com.perrigogames.life4

import java.text.DecimalFormat

actual val isDebug: Boolean get() = false

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun log(key: String, message: String) {
    print("")
}

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
