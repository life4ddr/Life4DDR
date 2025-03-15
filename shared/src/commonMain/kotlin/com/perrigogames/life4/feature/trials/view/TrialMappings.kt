package com.perrigogames.life4.feature.trials.view

import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.enums.TrialRank

fun Trial.toUIJacket(bestSession: SelectBestSessions?) = UITrialJacket(
    trial = this,
    session = bestSession,
    rank = bestSession?.goalRank,
    exScore = bestSession?.exScore?.toInt(),
    tintOnRank = TrialRank.entries.last(),
    showExRemaining = false,
)