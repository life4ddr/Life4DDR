package com.perrigogames.life4trials.data

import java.io.Serializable

class RankGoalSet(val rank: String,
                  val requirements: Int?,
                  val goals: List<BaseRankGoal>): Serializable