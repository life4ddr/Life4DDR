package com.perrigogames.life4

import platform.Foundation.NSDate
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

internal actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual val isDebug = true //FIXME

actual fun Int.longNumberString(): String {
    return NSNumberFormatter().let { format ->
        format.usesGroupingSeparator = true
        //format.locale = NSLocale.currentLocale() TODO figure out locale
        format.stringFromNumber(NSNumber(this))!!
    }
}