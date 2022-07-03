package com.perrigogames.life4

import platform.Foundation.NSDate
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

//FIXME
internal actual fun logE(key: String, message: String) = Unit
internal actual fun logException(t: Throwable) = Unit
internal actual fun setCrashInt(key: String, v: Int) = Unit
internal actual fun setCrashString(key: String, v: String) = Unit

actual val isDebug = true //FIXME

actual fun Int.longNumberString(): String {
    return NSNumberFormatter().let { format ->
        format.usesGroupingSeparator = true
        //format.locale = NSLocale.currentLocale() TODO figure out locale
        format.stringFromNumber(NSNumber(this))!!
    }
}