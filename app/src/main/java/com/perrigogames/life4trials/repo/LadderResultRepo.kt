package com.perrigogames.life4trials.repo

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4trials.db.ChartDB_
import com.perrigogames.life4trials.db.LadderResultDB
import com.perrigogames.life4trials.db.LadderResultDB_
import io.objectbox.BoxStore
import org.koin.core.KoinComponent
import org.koin.core.inject

class LadderResultRepo: KoinComponent {

    private val objectBox: BoxStore by inject()
    private val ladderResultBox get() = objectBox.boxFor(LadderResultDB::class.java)

    //
    // Queries
    //
    private val ladderResultQuery = ladderResultBox.query()
        .`in`(LadderResultDB_.chartId, LongArray(0)).parameterAlias("ids")
        .build()
    private val mfcQuery = ladderResultBox.query()
        .equal(LadderResultDB_.clearType, ClearType.MARVELOUS_FULL_COMBO.stableId)
        .apply {
            link(LadderResultDB_.chart)
                .`in`(ChartDB_.difficultyClass, longArrayOf(DifficultyClass.DIFFICULT.stableId, DifficultyClass.EXPERT.stableId, DifficultyClass.CHALLENGE.stableId))
        }
        .build()

    val isEmpty get() = ladderResultBox.isEmpty

    fun getResultsById(ids: LongArray): List<LadderResultDB> = ladderResultQuery.setParameters("ids", ids).find()

    fun getMFCs(): List<LadderResultDB> = mfcQuery.find()

    fun addResult(result: LadderResultDB) = ladderResultBox.put(result)

    fun clearRepo() = ladderResultBox.removeAll()
}
