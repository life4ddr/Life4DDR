package com.perrigogames.life4

import java.text.DecimalFormat

actual fun Int.longNumberString(): String = DecimalFormat("#,###,###").format(this)
