package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.data.SongResult
import com.perrigogames.life4trials.data.TrialRank
import com.perrigogames.life4trials.data.TrialSession
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class TrialSessionDB(var trialId: String,
                          var date: Date,
                          var goalRankString: String,
                          var goalObtained: Boolean,
                          @Id var id: Long = 0) {

    val goalRank get() = TrialRank.parse(goalRankString)

    @Backlink(to = "session")
    lateinit var songs: ToMany<SongDB>

    companion object {
        fun from(session: TrialSession) = TrialSessionDB(session.trial.id, Date(), session.goalRank.name, session.goalObtained)
    }
}

@Entity
data class SongDB(var score: Int = 0,
                  var exScore: Int = 0,
                  var misses: Int = 0,
                  var judge: Int = 0,
                  @Id var id: Long = 0) {

    lateinit var session: ToOne<TrialSessionDB>

    companion object {
        fun from(result: SongResult) = SongDB(
            result.score ?: 0,
            result.exScore ?: 0,
            result.misses ?: 0,
            result.badJudges ?: 0,
            0)
    }
}