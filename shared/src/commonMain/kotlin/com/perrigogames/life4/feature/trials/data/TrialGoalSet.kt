@file:UseSerializers(
    TrialTypeSerializer::class,
    TrialRankSerializer::class,
    PlayStyleSerializer::class,
    ChartTypeSerializer::class,
    ClearTypeSerializer::class)

package com.perrigogames.life4.feature.trials.data

import com.perrigogames.life4.enums.ChartTypeSerializer
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.ClearTypeSerializer
import com.perrigogames.life4.enums.PlayStyleSerializer
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.enums.TrialRankSerializer
import com.perrigogames.life4.feature.trials.enums.TrialTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Describes the goals required to obtain a specific [TrialRank].
 * @param rank The rank that this goal set is intended to grant upon completion.
 * @param clear A list of [ClearType]s required for each song in the [Trial]. Must contain 4 elements. If null, it is
 *  assumed that all songs must receive [ClearType.CLEAR].
 * @param clearIndexed A list of [ClearType]s, bound to song order, required for the songs in the [Trial]. Must contain
 *  4 elements. If null, this is not checked.
 * @param score A list of scores required for the songs in the [Trial].  Order does not matter as long as every entry
 *  has a unique song with a higher score. Must contain 4 elements. If null, this is not checked.
 * @param scoreIndexed A list of scores, bound to song order, required for the songs in the [Trial]. Must contain 4
 * elements. If null, this is not checked.
 * @param judge The number of allowed bad judgments (Greats, Goods and Misses) allowed across the entire [Trial]. If
 *  null, this is not checked.
 * @param miss The total number of misses allowed across the entire [Trial]. If null, this is not checked.
 * @param missEach The number of misses allowed for each song to satisfy this goal set.  If null, this is not checked.
 * @param exMissing The number of EX Score that is allowed to be missing to satisfy this goal set.  If null, this
 *  is not checked.
 */
@Serializable
data class TrialGoalSet(
    val rank: TrialRank,
    val clear: List<ClearType>? = null,
    @SerialName("clear_indexed") val clearIndexed: List<ClearType>? = null,
    val score: List<Int>? = null,
    @SerialName("score_indexed") val scoreIndexed: List<Int>? = null,
    val judge: Int? = null,
    val miss: Int? = null,
    @SerialName("miss_each") val missEach: Int? = null,
    @SerialName("ex_missing") val exMissing: Int? = null,
) {

    val goalTypes: List<GoalType>
        get() = mutableListOf<GoalType>().apply {
            if (clear != null || clearIndexed != null) add(GoalType.CLEAR)
            if (score != null || scoreIndexed != null) add(GoalType.SCORE)
            if (exMissing != null) add(GoalType.EX)
            if (judge != null) add(GoalType.BAD_JUDGEMENT)
            if (miss != null) add(GoalType.MISS)
        }

    @Serializable
    enum class GoalType {
        CLEAR, SCORE, EX, BAD_JUDGEMENT, MISS
    }
}
