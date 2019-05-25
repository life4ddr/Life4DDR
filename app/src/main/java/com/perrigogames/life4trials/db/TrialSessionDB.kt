package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.data.SongResult
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class TrialSessionDB(@Id var id: Long = 0) {

    @Backlink(to = "session")
    lateinit var songs: ToMany<SongDB>
}

@Entity
data class SongDB(@Id var id: Long = 0,
                  var score: Int,
                  var ex_score: Int,
                  var misses: Int,
                  var judge: Int) {

    lateinit var session: ToOne<TrialSessionDB>

    companion object {
        fun from(result: SongResult) = SongDB(0,
            result.score ?: 0,
            result.exScore ?: 0,
            result.misses ?: 0,
            result.badJudges ?: 0)
    }
}