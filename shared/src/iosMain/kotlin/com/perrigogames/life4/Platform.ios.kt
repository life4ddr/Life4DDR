package com.perrigogames.life4

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual val isDebug = true // FIXME

actual fun Int.longNumberString(): String {
    return NSNumberFormatter().let { format ->
        format.usesGroupingSeparator = true
        // format.locale = NSLocale.currentLocale() TODO figure out locale
        format.stringFromNumber(NSNumber(this))!!
    }
}
