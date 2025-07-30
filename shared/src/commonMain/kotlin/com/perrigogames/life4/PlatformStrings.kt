package com.perrigogames.life4

import dev.icerock.moko.resources.desc.Composition
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

fun List<StringDesc>.toListString(useAnd: Boolean, caps: Boolean): StringDesc = when {
    this.size == 1 -> this.first()
    else -> StringDesc.Composition(
        args = mapIndexed { index, d ->
            when (index) {
                this.lastIndex -> StringDesc.ResourceFormatted(when {
                    useAnd -> if (caps) MR.strings.and_s_caps else MR.strings.and_s
                    else -> if (caps) MR.strings.or_s_caps else MR.strings.or_s
                }, d)
                else -> d
            }
        },
        separator = ", "
    )
}

fun List<String>.toStringDescs() = map { StringDesc.Raw(it) }