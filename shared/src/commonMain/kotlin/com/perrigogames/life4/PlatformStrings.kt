package com.perrigogames.life4

import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.enums.*
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * An interface for providing text relating to LIFE4 data types to the
 * shared module.
 */
@OptIn(ExperimentalSerializationApi::class)
object PlatformStrings {

    fun nameString(rank: LadderRank) = StringDesc.ResourceFormatted(rank.nameRes)
    fun groupNameString(rank: LadderRank) = StringDesc.ResourceFormatted(rank.groupNameRes)
    fun nameString(rank: TrialRank) = StringDesc.ResourceFormatted(rank.nameRes)
    fun nameString(rank: PlacementRank) = StringDesc.ResourceFormatted(rank.nameRes)
    fun nameString(clazz: LadderRankClass) = StringDesc.ResourceFormatted(clazz.nameRes)
    fun lampString(ct: ClearType) = StringDesc.ResourceFormatted(ct.lampRes)
    fun clearString(ct: ClearType) = StringDesc.ResourceFormatted(ct.clearRes)
    fun clearStringShort(ct: ClearType) = StringDesc.ResourceFormatted(ct.clearResShort)
}

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