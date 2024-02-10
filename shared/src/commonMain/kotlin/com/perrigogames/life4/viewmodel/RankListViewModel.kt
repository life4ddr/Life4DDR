package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.firstrun.FirstRunSettingsManager
import com.perrigogames.life4.feature.firstrun.InitState
import com.perrigogames.life4.feature.laddergoals.UILadderData
import com.perrigogames.life4.feature.profile.UserInfoSettings
import com.perrigogames.life4.model.LadderDataManager
import com.perrigogames.life4.model.mapping.LadderGoalMapper
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RankListViewModel(
    isFirstRun: Boolean = false
) : ViewModel(), KoinComponent {

    private val firstRunSettingsManager: FirstRunSettingsManager by inject()
    private val userInfoSettings: UserInfoSettings by inject()
    private val ladderDataManager: LadderDataManager by inject()
    private val ladderGoalMapper: LadderGoalMapper by inject()

    private val _state = MutableStateFlow(UIRankList()).cMutableStateFlow()
    val state: StateFlow<UIRankList> = _state

    init {
        _state.value = _state.value.copy(
            ranks = (LadderRank.entries.toMutableList() as MutableList<LadderRank?>).apply {
                add(0, null)
            },
            noRank = when {
                isFirstRun -> UINoRank.FIRST_RUN
                else -> UINoRank.DEFAULT
            },
            firstRun = if(isFirstRun) {
                UIFirstRunRankList()
            } else {
                null
            }
        )
    }

    fun setRankSelected(rank: LadderRank?) {
        val entry = ladderDataManager.findRankEntry(rank)
        _state.value = _state.value.copy(
            ladderData = entry?.allGoals?.let { goals ->
                UILadderData(
                    items = goals.map { goal ->
                        ladderGoalMapper.toViewData(goal)
                    },
                    allowCompleting = false,
                    allowHiding = false
                )
            }
        )
    }

    fun moveToPlacements() {
        firstRunSettingsManager.setInitState(InitState.PLACEMENTS)
    }

    fun saveRank(ladderRank: LadderRank?) {
        firstRunSettingsManager.setInitState(InitState.DONE)
        userInfoSettings.setRank(ladderRank)
    }
}

data class UIRankList(
    val titleText: StringResource = MR.strings.select_a_starting_rank,
    val ranks: List<LadderRank?> = emptyList(),
    val noRank: UINoRank = UINoRank.DEFAULT,
    val footerText: StringResource = MR.strings.change_rank_later,
    val firstRun: UIFirstRunRankList? = null,
    val ladderData: UILadderData? = null,
)

data class UIFirstRunRankList(
    val buttonText: StringResource = MR.strings.play_placement
)

data class UINoRank(
    val bodyText: StringResource,
    val buttonText: StringResource,
) {

    companion object {
        val DEFAULT = UINoRank(
            bodyText = MR.strings.no_rank_goals,
            buttonText = MR.strings.i_have_no_rank
        )
        val FIRST_RUN = UINoRank(
            bodyText = MR.strings.no_rank_goals,
            buttonText = MR.strings.start_with_no_rank
        )
    }
}
