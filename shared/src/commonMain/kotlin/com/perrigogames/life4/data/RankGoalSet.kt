package com.perrigogames.life4.data

import kotlinx.serialization.Serializable

@Serializable
class RankGoalSet(val rank: String,
                  val requirements: Int?,
                  val goals: List<BaseRankGoal>)
