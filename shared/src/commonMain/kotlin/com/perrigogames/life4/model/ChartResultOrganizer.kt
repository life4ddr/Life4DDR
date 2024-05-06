package com.perrigogames.life4.model

import co.touchlab.kermit.Logger
import com.perrigogames.life4.GameConstants.MAX_SCORE
import com.perrigogames.life4.db.DetailedChartInfo
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songlist.IgnoreListManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songresults.ChartResultPair
import com.perrigogames.life4.feature.songresults.ResultDatabaseHelper
import com.perrigogames.life4.injectLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToLong

class ChartResultOrganizer : KoinComponent {
    private val ignoreListManager: IgnoreListManager by inject()
    private val resultDbHelper: ResultDatabaseHelper by inject()
    private val songDataManager: SongDataManager by inject()
    private val logger: Logger by injectLogger("ChartResultOrganizer")

    private lateinit var ignoredCharts: Set<DetailedChartInfo>
    private lateinit var matchedResults: Map<PlayStyle, Map<Int, ChartResultSet>>

    @Suppress("UNCHECKED_CAST")
    internal fun refresh() {
        ignoredCharts = emptySet() // FIXME ignore lists

        val tempResults = resultDbHelper.selectAll().toMutableSet()
        matchedResults =
            songDataManager.detailedCharts
                .groupBy { it.playStyle }
                .mapValues { (_, chartsByPlayStyle) ->
                    chartsByPlayStyle.groupBy { it.difficultyNumber.toInt() }
                        .mapValues { (_, chartsByDiffNum) ->
                            val pairs =
                                chartsByDiffNum.associateWith { chart ->
                                    tempResults.firstOrNull { result ->
                                        chart.id == result.chartId
                                    }.also { tempResults.remove(it) }
                                }.map { (chart, result) -> ChartResultPair(chart, result) }
                            ChartResultSet(pairs)
                        }
                }
    }

    fun getResults(
        playStyle: PlayStyle,
        diffNum: Int,
        populated: Boolean,
        filterIgnored: Boolean,
    ): ChartResults =
        matchedResults[playStyle]!![diffNum]!!.filter(
            FilterConfiguration(populated = populated, filterIgnored = filterIgnored),
        )

    inner class ChartResultSet(
        private val results: List<ChartResultPair>,
    ) {
        private val cache = mutableMapOf<FilterConfiguration, ChartResults>()

        fun filter(config: FilterConfiguration): ChartResults {
            return cache[config] ?: createConfiguration(config).also { cache[config] = it }
        }

        private fun createConfiguration(config: FilterConfiguration?): ChartResults {
            var out = results
            if (config?.populated == true) {
                out = out.filter { it.result != null }
            }
            if (config?.filterIgnored == true) {
                out = out.filter { !ignoredCharts.contains(it.chart) }
            }
            return ChartResults(out)
        }
    }

    data class ChartResults(
        val results: List<ChartResultPair>,
    ) {
        /**
         * Groups the songs by their truncated score in tens of thousands. For example, a result
         * with a score of 957,370 would appear under the key 95.  The returned list is sorted in
         * descending order of key, and each group's list is sorted in descending order of score.
         */
        val byScoreInTenThousands: List<Pair<Int, List<ChartResultPair>>> =
            results.groupBy { (it.result.safeScore / 10_000).toInt() }
                .mapValues { (_, group) -> group.sortedByDescending { it.result.safeScore } }
                .toList()
                .sortedByDescending { it.first }

        fun scoresInRange(
            bottom: Int? = null,
            top: Int? = null,
        ): List<ChartResultPair> {
            when {
                (bottom ?: 0) < 0 -> error("Bottom of range exceeds minimum (0)")
                (top ?: MAX_SCORE) > MAX_SCORE -> error("Top of range exceeds maximum ($MAX_SCORE)")
            }
            val bottomIdx = ((bottom ?: 0) / 10_000)
            val topIdx = ((top ?: MAX_SCORE) / 10_000)
            val centerSongs =
                (bottomIdx + 1 until topIdx)
                    .mapNotNull { scoresForGroup(it) }
                    .flatten()
            val bottomSongs =
                if (bottom != null) {
                    scoresForGroup(bottomIdx).filter { it.result.safeScore >= bottom }
                } else {
                    scoresForGroup(bottomIdx)
                }
            val topSongs =
                if (top == null) {
                    scoresForGroup(topIdx) // MFCs, group 100
                } else {
                    scoresForGroup(topIdx).filter { it.result.safeScore <= top }
                }
            return topSongs + centerSongs + bottomSongs
        }

        private fun scoresForGroup(group: Int): List<ChartResultPair> {
            val idx = byScoreInTenThousands.binarySearch { group - it.first }
            return if (idx >= 0) {
                byScoreInTenThousands[idx].second
            } else {
                emptyList()
            }
        }

        /**
         * Groups the songs by their clear type.
         */
        val byClearType: Map<ClearType, List<ChartResultPair>> =
            results.groupBy { it.result.safeClear }

        /**
         * Calculates the average score of all songs in this group
         */
        val averageScore =
            if (results.isNotEmpty()) {
                (results.sumOf { it.result.safeScore } / results.size.toDouble())
                    .roundToLong()
            } else {
                0
            }
    }
}

data class FilterConfiguration(
    val populated: Boolean,
    val filterIgnored: Boolean,
)
