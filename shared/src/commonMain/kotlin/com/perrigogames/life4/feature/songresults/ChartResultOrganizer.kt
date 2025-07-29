package com.perrigogames.life4.feature.songresults

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.MAPointsGoal
import com.perrigogames.life4.data.MAPointsStackedGoal
import com.perrigogames.life4.data.SongsClearGoal
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.util.split
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.plus

typealias OrganizerBase = Map<PlayStyle, DifficultyClassMap>
typealias DifficultyClassMap = Map<DifficultyClass, DifficultyNumberMap>
typealias DifficultyNumberMap = Map<Int, List<ChartResultPair>>

class ChartResultOrganizer: BaseModel(), KoinComponent {

    private val songResultsManager: SongResultsManager by inject()
    private val songResultSettings: SongResultSettings by inject()
    private val logger: Logger by injectLogger("ChartResultOrganizer")

    private val basicOrganizer = MutableStateFlow<OrganizerBase>(emptyMap()).cMutableStateFlow()

    private val chartListCache = mutableMapOf<ChartFilterState, Flow<List<ChartResultPair>>>()

    init {
        mainScope.launch {
            songResultsManager.library.map { base ->
                base.groupByPlayStyle().mapValues { (_, l1) ->
                    l1.groupByDifficultyClass().mapValues { (_, l2) ->
                        l2.groupByDifficultyNumber()
                    }
                }
            }
            .collect(basicOrganizer)
        }
    }

    fun chartsForConfig(config: ChartFilterState) : Flow<List<ChartResultPair>> {
        return if (config in chartListCache) {
            chartListCache[config]!!
        } else {
            basicOrganizer
                .map { it[config.selectedPlayStyle] ?: emptyMap() }
                .map { diffClassMap: DifficultyClassMap ->
                    val result = config.difficultyClassSelection.flatMap { diffClass ->
                        val diffNumMap = diffClassMap[diffClass] ?: emptyMap()
                        config.difficultyNumberRange.flatMap { diffNum ->
                            diffNumMap[diffNum] ?: emptyList()
                        }
                    }
                    return@map when(config.ignoreFilterType) {
                        IgnoreFilterType.BASIC -> {
                            result.filterNot { it.chart.song.deleted || (it.chart.lockType ?: 0) in BASIC_LOCKS }
                        }
                        IgnoreFilterType.EXPANDED -> {
                            result.filterNot { it.chart.song.deleted || (it.chart.lockType ?: 0) in EXPANDED_LOCKS }
                        }
                        IgnoreFilterType.ALL_ACTIVE -> {
                            result.filterNot { it.chart.song.deleted }
                        }
                        IgnoreFilterType.ALL -> result
                    }
                }
                .also { chartListCache[config] = it }
        }
    }

    fun resultsForConfig(
        base: BaseRankGoal?,
        config: FilterState,
        enableDifficultyTiers: Boolean
    ): Flow<ResultsBundle> {
        return chartsForConfig(config.chartFilter)
            .map { ChartFilterer(it) }
            .map { filterer ->
//                val start = Clock.System.now()
                val results = filterer.filtered(config.resultFilter)
//                val filterEnd = Clock.System.now()

                val goalExceptionScore = (base as? SongsClearGoal)?.exceptionScore
                val (resultsDone, resultsNotDone) = if (goalExceptionScore != null) {
                    val floorAchieved = mutableListOf<ChartResultPair>()
                    val floorNotAchieved = mutableListOf<ChartResultPair>()
                    results.resultsNotDone.forEach { pair ->
                        if ((pair.result?.score ?: 0) >= goalExceptionScore) {
                            floorAchieved.add(pair)
                        } else {
                            floorNotAchieved.add(pair)
                        }
                    }
                    (results.resultsDone + floorAchieved) to floorNotAchieved
                } else {
                    results.resultsDone to results.resultsNotDone
                }
                results.copy(
                    resultsDone = resultsDone.specialSorted(base, enableDifficultyTiers),
                    resultsNotDone = resultsNotDone.specialSorted(base, enableDifficultyTiers)
                )
//                ).also {
//                    val sortEnd = Clock.System.now()
//                    val filterMillis = start.until(filterEnd, DateTimeUnit.MILLISECOND)
//                    val sortMillis = filterEnd.until(sortEnd, DateTimeUnit.MILLISECOND)
//                    val totalMillis = filterMillis + sortMillis
//                    logger.d { "Filter time: ${filterMillis}ms, Sort time: ${sortMillis}ms, Total time: ${totalMillis}ms" }
//                }
            }
    }

    private fun List<ChartResultPair>.specialSorted(
        base: BaseRankGoal?,
        enableDifficultyTiers: Boolean
    ): List<ChartResultPair> = sortedWith { a, b ->
        if (base is MAPointsGoal || base is MAPointsStackedGoal) {
            // First, MA points, descending
            val maCompare = ((b.maPointsForDifficulty() - a.maPointsForDifficulty()) * 10000).toInt()
            if (maCompare != 0) {
                return@sortedWith maCompare
            }
        } else {
            // First, difficulty number, ascending
            val diffCompare = if (enableDifficultyTiers) {
                ((a.chart.combinedDifficultyNumber - b.chart.combinedDifficultyNumber) * 1000).toInt()
            } else {
                a.chart.difficultyNumber - b.chart.difficultyNumber
            }
            if (diffCompare != 0) {
                return@sortedWith diffCompare
            }

            // Then, by score, descending
            val scoreCompare = ((b.result?.score ?: 0) - (a.result?.score ?: 0)).toInt()
            if (scoreCompare != 0) {
                return@sortedWith scoreCompare
            }
        }

        // Otherwise, by name, ascending
        return@sortedWith a.chart.song.title.compareTo(b.chart.song.title)
    }

    companion object {
        const val ASIA_EXCLUSIVE = 10
        const val GOLD_CAB = 20
        const val GRAND_PRIX = 190
        const val FLARE_LOCKED = 250
        const val TIME_EVENT_LOCKED = 260
        const val UNNAMED_2 = 270
        const val EXTRA_SAVIOR = 280
        const val UNNAMED_1 = 290

        val BASIC_LOCKS = listOf(ASIA_EXCLUSIVE, GOLD_CAB, GRAND_PRIX, FLARE_LOCKED, TIME_EVENT_LOCKED, EXTRA_SAVIOR, UNNAMED_1, UNNAMED_2)
        val EXPANDED_LOCKS = listOf(ASIA_EXCLUSIVE, GOLD_CAB, GRAND_PRIX, TIME_EVENT_LOCKED, UNNAMED_1, UNNAMED_2)
    }
}

class ChartFilterer(
    private val chartResults: List<ChartResultPair>
) {

    fun all(): List<ChartResultPair> = chartResults

    fun filtered(config: ResultFilterState): ResultsBundle {
        // TODO search cache for more simple versions of the search
        val (done, notDone) = chartResults.split { chart ->
            config.clearTypeRange.contains(chart.result?.clearType?.ordinal ?: 0)
                    && config.scoreRange.contains(chart.result?.score ?: 0)
        }
        // TODO implement some caching
        return ResultsBundle(done, notDone)
    }
}

data class ResultsBundle(
    val resultsDone: List<ChartResultPair> = emptyList(),
    val resultsNotDone: List<ChartResultPair> = emptyList()
)

fun List<ChartResultPair>.groupByPlayStyle(): Map<PlayStyle, List<ChartResultPair>> =
    groupBy { it.chart.playStyle }

fun List<ChartResultPair>.groupByDifficultyClass(): Map<DifficultyClass, List<ChartResultPair>> =
    groupBy { it.chart.difficultyClass }

fun List<ChartResultPair>.groupByDifficultyNumber(): Map<Int, List<ChartResultPair>> =
    groupBy { it.chart.difficultyNumber }

fun List<ChartResultPair>.groupByClearType(): Map<ClearType, List<ChartResultPair>> =
    groupBy { it.result?.clearType ?: ClearType.NO_PLAY }

