package com.perrigogames.life4

import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

fun List<StringDesc>.toListString(useAnd: Boolean, caps: Boolean): List<StringDesc> = mapIndexed { index, d ->
    when {
        this@toListString.size == 1 -> d
        index == this@toListString.lastIndex -> StringDesc.ResourceFormatted(when {
            useAnd -> if (caps) MR.strings.and_s_caps else MR.strings.and_s
            else -> if (caps) MR.strings.or_s_caps else MR.strings.or_s
        }, d)
        index == this@toListString.lastIndex - 1 -> StringDesc.Raw("$d ")
        else -> StringDesc.Raw("$d, ")
    }
}

fun List<String>.toStringDescs() = map { StringDesc.Raw(it) }