package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Uid
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class TrialSessionDB(var trialId: String,
                          var date: Date,
                          var goalRankId: Int,
                          var goalObtained: Boolean,
                          @Id var id: Long = 0) {

    val goalRank get() = TrialRank.parse(goalRankId.toLong())

    val exScore get() = if (songResults.size > 0) songResults.sumBy { it.exScore } else null

    @Backlink(to = "session")
    lateinit var songResults: ToMany<TrialSongResultDB>

    companion object {
        fun from(session: TrialSession) = TrialSessionDB(
            session.trial.id,
            Date(),
            session.goalRank.stableId.toInt(),
            session.goalObtained)
    }
}

@Uid(897729382389109926L)
@Entity
data class TrialSongResultDB(var position: Int = 0,
                             var score: Int = 0,
                             var exScore: Int = 0,
                             var misses: Int = -1,
                             var badJudges: Int = -1,
                             var perfects: Int = -1,
                             var passed: Boolean = true,
                             @Id var id: Long = 0) {

    lateinit var session: ToOne<TrialSessionDB>

    companion object {
        fun from(result: SongResult, position: Int) = TrialSongResultDB(position,
            result.score ?: 0,
            result.exScore ?: 0,
            result.misses ?: 0,
            result.badJudges ?: 0,
            result.perfects ?: 0,
            result.passed,
            0)
    }
}