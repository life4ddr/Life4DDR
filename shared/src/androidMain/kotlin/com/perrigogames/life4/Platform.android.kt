package com.perrigogames.life4

import java.text.DecimalFormat

actual val isDebug: Boolean get() = false // FIXME

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
