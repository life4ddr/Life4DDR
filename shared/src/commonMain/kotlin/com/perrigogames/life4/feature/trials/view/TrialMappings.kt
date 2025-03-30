package com.perrigogames.life4.feature.trials.view

import com.perrigogames.life4.MR
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.enums.TrialRank
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

fun Trial.toUIJacket(bestSession: SelectBestSessions?) = UITrialJacket(
    trial = this,
    session = bestSession,
    rank = bestSession?.goalRank,
    exScore = bestSession?.exScore?.let { StringDesc.ResourceFormatted(MR.strings.ex_score_string_format, it) },
    tintOnRank = TrialRank.entries.last(),
    showExRemaining = false,
)