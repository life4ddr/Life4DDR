package com.perrigogames.life4.feature.songresults

import co.touchlab.kermit.Logger
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.feature.songlist.Chart
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songlist.SongLibrary
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.inject
import kotlin.random.Random

class SongResultsManager: BaseModel() {

    private val logger: Logger by injectLogger("SongResultsManager")
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()

    private val results = MutableStateFlow<List<ChartResult>>(emptyList()).cMutableStateFlow()
    private val _library = MutableStateFlow<List<ChartResultPair>>(emptyList()).cMutableStateFlow()
    val library: CStateFlow<List<ChartResultPair>> = _library.cStateFlow()

    init {
        mainScope.launch {
            combine(
                songDataManager.libraryFlow,
                results
            ) { songData, results ->
                logger.d { "Updating with ${songData.charts.size} charts and ${results.size} results" }
                matchCharts(songData, results)
            }
                .collect(_library)
        }
        refresh()
    }

    fun refresh() {
        logger.d("Refreshing song results")
        mainScope.launch {
            results.emit(resultDbHelper.selectAll())
        }
    }

    fun createDebugScores() {
        logger.d("Adding debug scores")
        mainScope.launch {
            val random = Random(Clock.System.now().toEpochMilliseconds())
            val charts = songDataManager.libraryFlow.value.charts
            results.emit(
                List(50) { random.nextInt(0, charts.size) }
                    .toSet()
                    .map { index -> charts[index] }
                    .map { chart ->
                        ChartResult(
                            skillId = chart.song.skillId,
                            difficultyClass = chart.difficultyClass,
                            playStyle = chart.playStyle,
                            clearType = ClearType.CLEAR,
                            score = 1_000_000L - (1_000 * random.nextInt(0, 100)),
                            exScore = 1_000L - random.nextInt(0, 100)
                        )
                    }
            )
        }
    }

    internal fun clearAllResults() {
        logger.w("Clearing all saved song results")
        mainScope.launch {
            resultDbHelper.deleteAll()
            results.emit(emptyList())
        }
    }

    private fun matchCharts(
        library: SongLibrary,
        results: List<ChartResult>
    ): List<ChartResultPair> {
        val matches = mutableMapOf<Chart, ChartResult>()
        results.forEach { result ->
            library.charts
                .firstOrNull { chart -> chart.matches(result) }
                ?.let { chart -> matches[chart] = result }
        }
        return library.charts.map { chart ->
            ChartResultPair(
                chart = chart,
                result = matches[chart]
            )
        }
    }
}
