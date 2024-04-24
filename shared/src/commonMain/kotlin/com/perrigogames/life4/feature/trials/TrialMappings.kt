package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.enums.TrialRank
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
fun Trial.toUIJacket(bestSession: SelectBestSessions?) = UITrialJacket(
    trial = this,
    session = bestSession,
    rank = bestSession?.goalRank,
    exScore = bestSession?.exScore?.toInt(),
    tintOnRank = TrialRank.entries.last(),
    showExRemaining = false,
)