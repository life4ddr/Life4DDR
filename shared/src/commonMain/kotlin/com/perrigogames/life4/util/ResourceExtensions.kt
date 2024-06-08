package com.perrigogames.life4.util

import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc

fun String.toDesc() = StringDesc.Raw(this)