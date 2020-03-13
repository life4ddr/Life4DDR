package com.perrigogames.life4trials

import android.content.Context
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.LadderRankClass
import com.perrigogames.life4.data.PlacementRank
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4trials.util.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidPlatformStrings: PlatformStrings, KoinComponent {

    private val c: Context by inject()

    override val rank = AndroidRankStrings(c)
    override val trial = AndroidTrialStrings(c)

    override fun nameString(rank: LadderRank) = c.getString(rank.nameRes)
    override fun groupNameString(rank: LadderRank) = c.getString(rank.groupNameRes)
    override fun nameString(rank: TrialRank) = c.getString(rank.nameRes)
    override fun nameString(rank: PlacementRank) = c.getString(rank.nameRes)
    override fun nameString(clazz: LadderRankClass) = c.getString(clazz.nameRes)
    override fun lampString(ct: ClearType) = c.getString(ct.lampRes)
    override fun clearString(ct: ClearType) = c.getString(ct.clearRes)
    override fun clearStringShort(ct: ClearType) = c.getString(ct.clearResShort)

    override fun toListString(list: List<String>, caps: Boolean): String = StringBuilder().apply {
        list.forEachIndexed { index, d ->
            append(when {
                list.size == 1 -> d
                index == list.lastIndex -> c.getString(if (caps) R.string.or_s_caps else R.string.or_s, d)
                index == list.lastIndex - 1 -> "$d "
                else -> "$d, "
            })
        }
    }.toString()
}
