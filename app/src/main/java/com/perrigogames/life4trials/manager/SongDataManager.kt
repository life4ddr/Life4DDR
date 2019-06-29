package com.perrigogames.life4trials.manager

import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.data.DifficultyClass
import com.perrigogames.life4trials.data.PlayStyle
import com.perrigogames.life4trials.db.ChartDB
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import io.objectbox.kotlin.query

/**
 * A Manager class that keeps track of the available songs
 */
class SongDataManager: BaseManager() {

    val songBox get() = objectBox.boxFor(SongDB::class.java)
    val chartBox get() = objectBox.boxFor(ChartDB::class.java)

    fun getSongByName(name: String): SongDB? {
        songBox.query { return equal(SongDB_.title, name).build().findFirst() }
        return null
    }

    fun getOrCreateSong(name: String): SongDB {
        return getSongByName(name) ?: SongDB(name).also {
            songBox.put(it)
        }
    }

    fun updateOrCreateChartForSong(song: SongDB,
                                   playStyle: PlayStyle,
                                   difficultyClass: DifficultyClass,
                                   difficultyNumber: Int): ChartDB {
        val chart = song.charts.firstOrNull { it.playStyle == playStyle && it.difficultyClass == difficultyClass }
        chart?.let {
            if (it.difficultyNumber != difficultyNumber) {
                Crashlytics.logException(UnexpectedDifficultyNumberException(it, difficultyNumber))
                it.difficultyNumber = difficultyNumber
                chartBox.put(it)
            }
        }
        return chart ?: ChartDB(difficultyClass, difficultyNumber, playStyle).also {
            song.charts.add(it)
            chartBox.put(it)
            songBox.put(song)
        }
    }

    fun updateChart(chart: ChartDB) = chartBox.put(chart)
}

class UnexpectedDifficultyNumberException(chart: ChartDB, newDiff: Int): Exception(
    "Chart ${chart.song.target.title} ${chart.playStyle} ${chart.difficultyClass} changed: ${chart.difficultyNumber} -> $newDiff")
