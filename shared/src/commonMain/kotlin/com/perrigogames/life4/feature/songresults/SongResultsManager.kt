package com.perrigogames.life4.feature.songresults

import co.touchlab.kermit.Logger
import com.perrigogames.life4.db.ChartResult
import com.perrigogames.life4.db.ResultDatabaseHelper
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class SongResultsManager: BaseModel() {

    private val logger: Logger by injectLogger("SongResultsManager")
    private val songDataManager: SongDataManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()

    private val results = MutableStateFlow<List<ChartResult>>(emptyList()).cMutableStateFlow()
    private val _library = MutableStateFlow<List<ChartResultPair>>(emptyList()).cMutableStateFlow()
    val library: StateFlow<List<ChartResultPair>> = _library.asStateFlow()

    init {
        mainScope.launch {
            combine(
                songDataManager.libraryFlow,
                results
            ) { songData, results ->
                logger.d { "Updating with ${songData.charts.size} charts and ${results.size} results" }
                songData.charts.map { chart ->
                    ChartResultPair(
                        chart = chart,
                        result = results.firstOrNull { chart.matches(it) }
                    )
                }
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

    internal fun clearAllResults() {
        logger.w("Clearing all saved song results")
        mainScope.launch {
            resultDbHelper.deleteAll()
            results.emit(emptyList())
        }
    }
}
