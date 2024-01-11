package com.perrigogames.life4.feature.songlist

import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.db.SongInfo
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject

class SortedSongsRepo : BaseModel() {

    private val songDataManager: SongDataManager by inject()

    private val libraryFlow = songDataManager.libraryFlow

    val groupedBySong: Flow<Map<SongInfo, List<DetailedChartInfo>>> = libraryFlow.map { library ->
        val groupedCharts = library.charts.groupBy { it.skillId }
        library.songs.associateWith { song ->
            groupedCharts[song.skillId]
                ?: error("Song ${song.title} (${song.skillId}) contains no charts")
        }
    }

    val groupedByPlayStyle: Flow<Map<PlayStyle, List<DetailedChartInfo>>> = libraryFlow.map { library ->
        library.charts.groupBy { it.playStyle }
    }

    fun chartsOfSong(matcher: (SongInfo) -> Boolean): Flow<List<DetailedChartInfo>> =
        groupedBySong.map { groups ->
            groups.keys.firstOrNull(matcher)?.let { groups[it] }
                ?: emptyList()
        }

    fun chartsOfPlayStyle(playStyle: PlayStyle): Flow<List<DetailedChartInfo>> =
        groupedByPlayStyle.map {
            it[playStyle] ?: error("Play Style $playStyle contains no charts")
        }
}

fun Flow<List<DetailedChartInfo>>.filterPlayStyle(playStyle: PlayStyle) =
    this.map { it.filterPlayStyle(playStyle) }

fun List<DetailedChartInfo>.filterPlayStyle(playStyle: PlayStyle) =
    filter { it.playStyle == playStyle }

fun Flow<List<DetailedChartInfo>>.filterDifficultyClass(diff: DifficultyClass) =
    this.map { it.filterDifficultyClass(diff) }

fun List<DetailedChartInfo>.filterDifficultyClass(diff: DifficultyClass) =
    filter { it.difficultyClass == diff }

fun Flow<List<DetailedChartInfo>>.filterDifficultyNumber(diff: Int) =
    this.map { it.filterDifficultyNumber(diff) }

fun List<DetailedChartInfo>.filterDifficultyNumber(diff: Int) =
    filter { it.difficultyNumber == diff.toLong() }