package com.perrigogames.life4.db

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.log
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongDatabaseHelper(sqlDriver: SqlDriver): DatabaseHelper(sqlDriver) {

    private val queries = dbRef.songDataQueries

    suspend fun insertSong(
        id: Long,
        skillId: String,
        title: String,
        artist: String?,
        version: GameVersion,
        preview: Boolean,
    ) = withContext(Dispatchers.Default) {
        queries.insertSong(skillId, id, title, artist, version, preview)
    }

    suspend fun insertChart(
        songSkillId: String,
        difficultyClass: DifficultyClass,
        difficultyNumber: Long,
        playStyle: PlayStyle,
    ) = withContext(Dispatchers.Default) {
        queries.insertChart(songSkillId, difficultyClass, difficultyNumber, playStyle)
    }

    suspend fun insertSongsAndCharts(
        songs: List<SongInfo>,
        charts: List<ChartInfo>,
    ) = withContext(Dispatchers.Default) {
        queries.transaction {
            songs.forEach { song ->
                queries.insertSong(song.skillId, song.id, song.title, song.artist, song.version, song.preview)
            }
            charts.forEach { chart ->
                queries.insertChart(chart.songSkillId, chart.difficultyClass, chart.difficultyNumber, chart.playStyle)
            }
        }
        log("SongImport", "Import committed")
    }

    fun selectSongBySkillID(title: String) = queries.selectSongBySkillId(title).executeAsOneOrNull()
    fun selectSongsBySkillID(titles: List<String>) = queries.selectSongBySkillIdList(titles).executeAsList()
    fun selectSongByTitle(title: String) = queries.selectSongByTitle(title).executeAsOneOrNull()
    fun allSongs() = queries.allSongs().executeAsList()
    fun allDetailedCharts() = queries.allDetailedCharts().executeAsList()

    fun selectChart(skillId: String, playStyle: PlayStyle, difficultyClass: DifficultyClass) =
        queries.selectChart(skillId, playStyle, difficultyClass).executeAsOneOrNull()
    fun selectChartsForSongList(skillIds: List<String>) =
        queries.selectChartsForSongList(skillIds).executeAsList()
    fun selectSongsAndCharts(ids: List<String>): Map<SongInfo, List<DetailedChartInfo>> {
        val songs = selectSongsBySkillID(ids)
        return selectChartsForSongList(ids).matchWithSongs(songs)
    }

    fun deleteAll() = queries.deleteAll()

    private fun List<DetailedChartInfo>.matchWithSongs(songs: List<SongInfo>) =
        groupBy { chart -> songs.first { it.skillId == chart.songSkillId } }
}

val ChartInfo.aggregateDiffStyleString get() = difficultyClass.aggregateString(playStyle)
val DetailedChartInfo.aggregateDiffStyleString get() = difficultyClass.aggregateString(playStyle)