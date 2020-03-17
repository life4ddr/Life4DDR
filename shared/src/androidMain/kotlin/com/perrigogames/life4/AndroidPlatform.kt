package com.perrigogames.life4

import android.util.Log
import java.text.DecimalFormat

actual val isDebug: Boolean get() = BuildConfig.DEBUG

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun log(key: String, message: String): Unit {
    Log.v(key, message)
}

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
