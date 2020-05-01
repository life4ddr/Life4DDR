package com.perrigogames.life4.db

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries = dbRef.songResultQueries

    suspend fun insertResult(songId: Long,
                             difficultyClass: DifficultyClass,
                             playStyle: PlayStyle,
                             clearType: ClearType,
                             exScore: Int? = null) = withContext(Dispatchers.Default) {
        queries.insertResult(songId, difficultyClass, playStyle, clearType, exScore?.toLong())
    }

    suspend fun insertResult(chart: ChartInfo,
                             clearType: ClearType,
                             exScore: Int? = null) =
        insertResult(chart.songId, chart.difficultyClass, chart.playStyle, clearType, exScore)

    fun deleteAll() {
        queries.deleteAll()
    }
}