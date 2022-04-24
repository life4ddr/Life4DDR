package com.perrigogames.life4.db

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.model.LadderImporter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries = dbRef.songResultQueries

    suspend fun insertResult(
        skillId: String,
        difficultyClass: DifficultyClass,
        playStyle: PlayStyle,
        clearType: ClearType,
        score: Int,
        exScore: Int? = null
    ) = withContext(Dispatchers.Default) {
        queries.insertResult(skillId, difficultyClass, playStyle, clearType, score.toLong(), exScore?.toLong())
    }

    suspend fun insertResult(
        song: SongInfo,
        chart: ChartInfo,
        clearType: ClearType,
        score: Int,
        exScore: Int? = null
    ) = insertResult(song.skillId, chart.difficultyClass, chart.playStyle, clearType, score, exScore)

    suspend fun insertResult(
        chart: DetailedChartInfo,
        clearType: ClearType,
        score: Int,
        exScore: Int? = null
    ) = insertResult(chart.skillId, chart.difficultyClass, chart.playStyle, clearType, score, exScore)

    suspend fun insertSAResults(entries: List<LadderImporter.SASongEntry>) {
        withContext(Dispatchers.Default) {
            queries.transaction {
                entries.forEach {
                    queries.insertResult(
                        it.skillId!!,
                        it.difficultyClass,
                        it.playStyle,
                        it.clearType,
                        it.score,
                        null
                    )
                }
            }
        }
    }

    fun selectAll() = queries.selectAll().executeAsList()

    fun selectMFCs() = queries.selectMFCs().executeAsList()

    fun selectCharts(chartIds: List<String>) = queries.selectCharts(chartIds).executeAsList()

    fun deleteAll() = queries.deleteAll()
}

fun DetailedChartInfo.toResult(
    clearType: ClearType = ClearType.FAIL,
    score: Long = 0,
    exScore: Long = 0,
) = ChartResult(
    skillId = this.skillId,
    difficultyClass = this.difficultyClass,
    playStyle = this.playStyle,
    clearType = clearType,
    score = score,
    exScore = exScore,
)