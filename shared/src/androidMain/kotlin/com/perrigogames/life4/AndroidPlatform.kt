package com.perrigogames.life4

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

internal actual fun logMessage(m: String) {
    FirebaseCrashlytics.getInstance().log(m)
}

internal actual fun logException(t: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(t)
}

internal actual fun setCrashInt(key: String, v: Int) {
    FirebaseCrashlytics.getInstance().setCustomKey(key, v)
}

internal actual fun setCrashString(key: String, v: String) {
    FirebaseCrashlytics.getInstance().setCustomKey(key, v)
}

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
