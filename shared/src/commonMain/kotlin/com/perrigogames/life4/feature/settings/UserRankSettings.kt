package com.perrigogames.life4.feature.settings

import com.perrigogames.life4.enums.LadderRank
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalSettingsApi::class)
class UserRankSettings : SettingsManager() {

    val rank: StateFlow<LadderRank?> = settings.getLongOrNullFlow(KEY_INFO_RANK)
        .map { LadderRank.parse(it) }
        .stateIn(mainScope, SharingStarted.Eagerly, null)

    private val _targetRank = settings.getLongOrNullFlow(KEY_INFO_TARGET_RANK)
        .map { LadderRank.parse(it) }

    /**
     * This flow emits the functional target rank depending on configuration.  Priority order is:
     * - Any specifically set target rank
     * - The rank immediately following your current rank (this is null if the rank is maxed out)
     * - [LadderRank.COPPER1], because you have no rank in this case
     */
    val targetRank: Flow<LadderRank?> = combine(_targetRank, rank) { target, actual ->
        when {
            target != null -> target
            actual != null -> actual.next
            else -> LadderRank.COPPER1
        }
    }

    fun setRank(rank: LadderRank?) = mainScope.launch {
        rank?.also { settings.putLong(KEY_INFO_RANK, it.stableId) } ?: settings.remove(KEY_INFO_RANK)
    }

    fun setTargetRank(rank: LadderRank?) = mainScope.launch {
        rank?.also { settings.putLong(KEY_INFO_TARGET_RANK, it.stableId) } ?: settings.remove(KEY_INFO_TARGET_RANK)
    }

    companion object {
        const val KEY_INFO_RANK = "KEY_INFO_RANK"
        const val KEY_INFO_TARGET_RANK = "KEY_INFO_TARGET_RANK"
    }
}