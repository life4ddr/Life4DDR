package com.perrigogames.life4trials.data

import java.io.Serializable

/**
 * Data class for deserializing the ranks.json file. Describes all of the ranks that can
 * be earned in LIFE4 and the goals required to obtain each.
 */
class LadderRankData(val ranks: List<RankEntry>): Serializable

/**
 * Describes a single rank in [LadderRankData] and the goals required to obtain it.
 */
class RankEntry(val rank: LadderRank,
                val goals: List<BaseRankGoal>): Serializable