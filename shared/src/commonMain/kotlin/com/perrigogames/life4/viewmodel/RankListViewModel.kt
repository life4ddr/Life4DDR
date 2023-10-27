package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.data.BaseRankGoal
import com.perrigogames.life4.data.LadderGoalProgress
import com.perrigogames.life4.data.RankEntry
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.BaseModel
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.LadderProgressManager
import com.perrigogames.life4.model.UserRankManager
import com.perrigogames.life4.util.ViewState
import com.perrigogames.life4.util.ifNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class RankListViewModel(private val config: RankListConfig) : BaseModel() {

    private val ladderDataManager: LadderDataManager by inject()
    private val userRankManager: UserRankManager by inject()
    private val ladderProgressManager: LadderProgressManager by inject()
    private val platformStrings: PlatformStrings by inject()

    private val _state = MutableStateFlow<ViewState<RankListViewData, String>>(ViewState.Loading)
    val state: StateFlow<ViewState<RankListViewData, String>> = _state.asStateFlow()

    private lateinit var entry: RankEntry

    init {
        mainScope.launch {
            ladderDataManager.findRankEntry(config.targetRank)
                ?.also { entry = it }
                .ifNull {
                    _state.value = ViewState.Error("No goals found for ${config.targetRank.name}")
                    return@launch
                }
            _state.value = ViewState.Success(
                RankListViewData(
                    ranks = entry.goals.map(::toViewData)
                )
            )
        }
    }

    private fun toViewData(base: BaseRankGoal): RankViewData {
        return RankViewData(
            id = base.id,
            title = base.goalString(platformStrings)
        )
    }
}

data class RankListConfig(
    val targetRank: LadderRank
)

data class RankListViewData(
    val ranks: List<RankViewData> = emptyList()
)

data class RankViewData(
    val id: Int,
    val title: String,
    val completeChecked: Boolean = false,
    val progress: LadderGoalProgress? = null
)