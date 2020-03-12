package com.perrigogames.life4

import com.perrigogames.life4.data.*

/**
 * An interface for providing text relating to LIFE4 data types to the
 * shared module.
 */
interface PlatformStrings {

    val rank: RankStrings
    val trial: TrialStrings

    val LadderRank.name: String
    val LadderRank.groupName: String
    val LadderRankClass.name: String
    val TrialRank.name: String
    val PlacementRank.name: String
    fun lampString(ct: ClearType): String
    fun clearString(ct: ClearType): String
    fun clearStringShort(ct: ClearType): String

    fun toListString(list: List<String>, caps: Boolean): String
}

/**
 * Formats an integer with separators (1234567 -> 1,234,567)
 */
expect fun Int.longNumberString(): String
