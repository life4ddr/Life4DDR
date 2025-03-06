package com.perrigogames.life4.feature.songresults

import com.perrigogames.life4.SettingsKeys.KEY_ENABLE_DIFFICULTY_TIERS
import com.perrigogames.life4.SettingsKeys.KEY_SONG_LIST_FILTER_STATE
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.settings.SettingsManager
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
class SongResultSettings : SettingsManager() {

    val enableDifficultyTiers: Flow<Boolean> =
        settings.getBooleanFlow(KEY_ENABLE_DIFFICULTY_TIERS, false)
            .distinctUntilChanged()

    fun setEnableDifficultyTiers(enabled: Boolean) = mainScope.launch {
        settings.putBoolean(KEY_ENABLE_DIFFICULTY_TIERS, enabled)
    }

    val songListFilterState: Flow<FilterState> =
        settings.getStringOrNullFlow(KEY_SONG_LIST_FILTER_STATE)
            .map { state ->
                state?.let { Json.decodeFromString(SerialFilterState.serializer(), it) }
                    ?.toFilterState()
                    ?: FilterState()
            }

    fun setSongListFilterState(state: FilterState) = mainScope.launch {
        settings.putString(
            KEY_SONG_LIST_FILTER_STATE,
            Json.encodeToString(SerialFilterState.serializer(), state.toSerialFilterState())
        )
    }
}

@Serializable
data class SerialFilterState(
    val selectedPlayStyle: PlayStyle,
    val difficultyClassSelection: List<DifficultyClass>,
    val difficultyNumberBottom: Int,
    val difficultyNumberTop: Int,
    val clearTypeBottom: Int,
    val clearTypeTop: Int,
    val scoreBottom: Int,
    val scoreTop: Int,
    val filterIgnored: Boolean,
) {

    fun toFilterState() = FilterState(
        selectedPlayStyle = selectedPlayStyle,
        difficultyClassSelection = difficultyClassSelection,
        difficultyNumberRange = difficultyNumberBottom..difficultyNumberTop,
        clearTypeRange = clearTypeBottom..clearTypeTop,
        scoreRange = scoreBottom..scoreTop,
        filterIgnored = filterIgnored
    )
}

fun FilterState.toSerialFilterState() = SerialFilterState(
    selectedPlayStyle = chartFilter.selectedPlayStyle,
    difficultyClassSelection = chartFilter.difficultyClassSelection,
    difficultyNumberBottom = chartFilter.difficultyNumberRange.first,
    difficultyNumberTop = chartFilter.difficultyNumberRange.last,
    clearTypeBottom = resultFilter.clearTypeRange.first,
    clearTypeTop = resultFilter.clearTypeRange.last,
    scoreBottom = resultFilter.scoreRange.first,
    scoreTop = resultFilter.scoreRange.last,
    filterIgnored = resultFilter.filterIgnored,
)
