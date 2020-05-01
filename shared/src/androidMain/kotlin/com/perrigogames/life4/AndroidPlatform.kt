package com.perrigogames.life4

import android.util.Log
import com.crashlytics.android.Crashlytics
import java.text.DecimalFormat

actual val isDebug: Boolean get() = BuildConfig.DEBUG

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun log(key: String, message: String) {
    Log.v(key, message)
}

internal actual fun logE(key: String, message: String) {
    Log.e(key, message)
}

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

internal actual fun logException(t: Throwable) {
    Crashlytics.logException(t)
}

internal actual fun setCrashInt(key: String, v: Int) {
    Crashlytics.setInt(key, v)
}

internal actual fun setCrashString(key: String, v: String) {
    Crashlytics.setString(key, v)
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
