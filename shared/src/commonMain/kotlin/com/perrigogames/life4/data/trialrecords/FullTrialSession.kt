package com.perrigogames.life4.data.trialrecords

import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong

data class FullTrialSession(
    val session: TrialSession,
    val songs: List<TrialSong>,
)
