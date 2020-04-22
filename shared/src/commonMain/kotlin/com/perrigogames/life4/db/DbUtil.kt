package com.perrigogames.life4.db

import com.soywiz.klock.DateTime
import com.soywiz.klock.ISO8601

val nowString get() = DateTime.now().format(ISO8601.DATETIME_COMPLETE)