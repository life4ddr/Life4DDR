package com.perrigogames.life4.db

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries = dbRef.songDataQueries

    suspend fun insertSong(id: Long,
                           title: String,
                           artist: String?,
                           version: GameVersion,
                           preview: Boolean) = withContext(Dispatchers.Default) {
        queries.insertSong(id, title, artist, version, preview)
    }

    suspend fun insertChart(songId: Long,
                            difficultyClass: DifficultyClass,
                            difficultyNumber: Long,
                            playStyle: PlayStyle) = withContext(Dispatchers.Default) {
        queries.insertChart(songId, difficultyClass, difficultyNumber, playStyle)
    }

    fun selectSong(id: Long) = queries.selectSongById(id).executeAsOneOrNull()
    fun selectSongs(ids: List<Long>) = queries.selectSongByIdList(ids).executeAsList()
    fun selectSongByTitle(title: String) = queries.selectSongByTitle(title).executeAsOneOrNull()
    fun allSongs() = queries.allSongs().executeAsList()

    fun selectChart(songId: Long, playStyle: PlayStyle, difficultyClass: DifficultyClass) =
        queries.selectChart(songId, playStyle, difficultyClass).executeAsOneOrNull()
    fun selectChartsForSong(songId: Long) =
        queries.selectChartsForSong(songId).executeAsList()
    fun selectChartsForSongList(songIds: List<Long>) =
        queries.selectChartsForSongList(songIds).executeAsList()
    fun selectChartsForSong(songId: Long, playStyle: PlayStyle) =
        queries.selectChartsForSongAndPlayStyle(songId, playStyle).executeAsList()
    fun selectChartsForDifficulty(songId: Long, difficultyClass: DifficultyClass) =
        queries.selectChartsForSongAndDifficulty(songId, difficultyClass)
    fun selectSongsAndCharts(songIds: List<Long>): Map<SongInfo, List<ChartInfo>> {
        val songs = selectSongs(songIds)
        return selectChartsForSongList(songIds).groupBy { chart -> songs.first { it.id == chart.songId } }
    }

    fun allSongsAndCharts() = queries.allSongsAndCharts().executeAsList()

    fun deleteAll() = queries.deleteAll()
}
