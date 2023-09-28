package com.perrigogames.life4

import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.ClearType

/**
 * An interface for providing text relating to LIFE4 data types to the
 * shared module.
 */
interface PlatformStrings {

    fun nameString(rank: LadderRank): String
    fun groupNameString(rank: LadderRank): String
    fun nameString(rank: TrialRank): String
    fun nameString(rank: PlacementRank): String
    fun nameString(clazz: LadderRankClass): String
    fun lampString(ct: ClearType): String
    fun clearString(ct: ClearType): String
    fun clearStringShort(ct: ClearType): String

    fun toListString(list: List<String>, useAnd: Boolean, caps: Boolean): String

    val rank: RankStrings
    val trial: TrialStrings
    val notification: NotificationStrings
}
