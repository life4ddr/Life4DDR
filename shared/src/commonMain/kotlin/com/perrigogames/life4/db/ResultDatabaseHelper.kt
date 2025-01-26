package com.perrigogames.life4.db

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.Chart
import com.perrigogames.life4.feature.songresults.LadderImporter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries = dbRef.songResultQueries

    suspend fun insertResult(
        chart: Chart,
        clearType: ClearType,
        score: Int,
        exScore: Int? = null,
        flare: Int? = null,
        flareSkill: Int? = null,
    ) = withContext(Dispatchers.Default) {
        queries.insertResult(
            skillId = chart.song.skillId,
            difficultyClass = chart.difficultyClass,
            playStyle = chart.playStyle,
            clearType = clearType,
            score = score.toLong(),
            exScore = exScore?.toLong(),
            flare = flare?.toLong(),
            flareSkill = flareSkill?.toLong()
        )
    }

    suspend fun insertResults(
        results: List<ChartResult>
    ) = withContext(Dispatchers.Default) {
        queries.transaction {
            results.forEach {
                queries.insertResult(
                    skillId = it.skillId,
                    difficultyClass = it.difficultyClass,
                    playStyle = it.playStyle,
                    clearType = it.clearType,
                    score = it.score,
                    exScore = it.exScore,
                    flare = it.flare,
                    flareSkill = it.flareSkill
                )
            }
        }
    }

    suspend fun insertSAResults(entries: List<LadderImporter.SASongEntry>) {
        withContext(Dispatchers.Default) {
            queries.transaction {
                entries.forEach {
                    queries.insertResult(
                        skillId = it.skillId,
                        playStyle = it.playStyle,
                        difficultyClass = it.difficultyClass,
                        clearType = it.clearType,
                        score = it.score,
                        exScore = null,
                        flare = null,
                        flareSkill = null,
                    )
                }
            }
        }
    }

    fun selectAll() = queries.selectAll().executeAsList()

    fun deleteAll() = queries.deleteAll()
}

fun Chart.toResult(
    clearType: ClearType = ClearType.FAIL,
    score: Long = 0,
    exScore: Long = 0,
    flare: Long? = null,
    flareSkill: Long? = null,
) = ChartResult(
    skillId = song.skillId,
    playStyle = playStyle,
    difficultyClass = difficultyClass,
    clearType = clearType,
    score = score,
    exScore = exScore,
    flare = flare,
    flareSkill = flareSkill
)