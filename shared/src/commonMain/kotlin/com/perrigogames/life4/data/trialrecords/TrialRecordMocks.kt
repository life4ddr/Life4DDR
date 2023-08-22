package com.perrigogames.life4.data.trialrecords

import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.db.TrialSong
import com.perrigogames.life4.enums.TrialRank
import kotlinx.datetime.Clock

object TrialRecordMocks {
    fun createTrialSession(
        id: Long = 1L,
        trialId: String = "mock_trial",
        date: String = Clock.System.now().toString(),
        goalRank: TrialRank,
        goalObtained: Boolean = true,
    ) = TrialSession(
        id = id,
        trialId = trialId,
        date = date,
        goalRank = goalRank,
        goalObtained = goalObtained,
    )

    fun createTrialSong(
        id: Long = 1L,
        sessionId: Long = 1L,
        position: Long = 1L,
        score: Long = 0,
        exScore: Long = 0,
        misses: Long? = null,
        goods: Long? = null,
        greats: Long? = null,
        perfects: Long? = null,
        passed: Boolean = true,
    ) = TrialSong(
        id = id,
        sessionId = sessionId,
        position = position,
        score = score,
        exScore = exScore,
        misses = misses,
        goods = goods,
        greats = greats,
        perfects = perfects,
        passed = passed,
    )
}