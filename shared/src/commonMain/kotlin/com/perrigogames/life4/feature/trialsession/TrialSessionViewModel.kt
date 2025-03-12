package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.*
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trialrecords.TrialRecordsManager
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.util.ViewState
import com.perrigogames.life4.util.toViewState
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.color.asColorDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageDesc
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialSessionViewModel(trialId: String) : KoinComponent, ViewModel() {

    private val songDataManager: SongDataManager by inject()
    private val userRankManager: UserRankManager by inject()
    private val trialManager: TrialManager by inject()
    private val trialSessionManager: TrialSessionManager by inject()
    private val trialRecordsManager: TrialRecordsManager by inject()

    private val trial = trialManager.trialsFlow.map { trials ->
        trials.firstOrNull { it.id == trialId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val targetRank = MutableStateFlow(TrialRank.BRONZE)
    private val _state = MutableStateFlow<ViewState<UITrialSession, Unit>>(
        ViewState.Loading
    ).cMutableStateFlow()
    val state: StateFlow<ViewState<UITrialSession, Unit>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            targetRank.collect { target ->
                val trial = trialManager.trialsFlow.value
                    .firstOrNull { it.id == trialId }
                    ?: return@collect
                val current = (_state.value as? ViewState.Success)?.data ?: return@collect
                if (current.targetRank is UITargetRank.Selection && current.targetRank.rank != target) {
                    _state.value = current.copy(
                        targetRank = current.targetRank.copy(
                            rank = target,
                            title = target.nameRes.desc(),
                            titleColor = target.colorRes.asColorDesc(),
                            availableRanks = current.targetRank.availableRanks,
                            rankGoalItems = TrialGoalStrings.generateGoalStrings(trial.goalSet(target)!!, trial)
                        )
                    ).toViewState()
                }
            }
        }

        val trial = trialManager.trialsFlow.value
            .firstOrNull { it.id == trialId }
        if (trial == null) {
            _state.value = ViewState.Error(Unit)
        } else {
            val bestSession = trialRecordsManager.bestSessions.value
                .firstOrNull { it.trialId == trialId }
            val allowedRanks = trial.goals?.map { it.rank } ?: emptyList()
            val rank = getStartingRank(
                playerRank = userRankManager.rank.value,
                bestRank = bestSession?.goalRank,
                allowedRanks = allowedRanks
            )
            val bestSessionExScore = bestSession?.exScore?.toInt() ?: 0
            _state.value = ViewState.Success(
                UITrialSession(
                    trialTitle = trial.name.desc(),
                    trialLevel = StringDesc.ResourceFormatted(
                        MR.strings.level_short_format,
                        trial.difficulty ?: 0
                    ),
                    backgroundImage = trial.coverResource ?: MR.images.trial_default.asImageDesc(),
                    exScoreBar = UIEXScoreBar(
                        labelText = MR.strings.ex.desc(),
                        currentEx = bestSessionExScore,
                        currentExText = StringDesc.Raw(bestSessionExScore.toString()),
                        maxEx = trial.totalEx,
                        maxExText = StringDesc.Raw("/" + trial.totalEx)
                    ),
                    targetRank = UITargetRank.Selection(
                        rank = rank,
                        title = rank.nameRes.desc(),
                        titleColor = rank.colorRes.asColorDesc(),
                        availableRanks = allowedRanks,
                        rankGoalItems = TrialGoalStrings.generateGoalStrings(trial.goalSet(rank)!!, trial),
                    ),
                    content = UITrialSessionContent.Summary(
                        items = trial.songs.map { song ->
                            val songInfo = songDataManager.getChart(
                                skillId = song.skillId,
                                playStyle = PlayStyle.SINGLE,
                                difficultyClass = song.difficultyClass,
                            ) ?: throw IllegalStateException("Song info not found for ${song.skillId} / ${song.difficultyClass}")
                            UITrialSessionContent.Summary.Item(
                                jacketUrl = song.url,
                                difficultyClassText = songInfo.difficultyClass.nameRes.desc(),
                                difficultyClassColor = songInfo.difficultyClass.colorRes.asColorDesc(),
                                difficultyNumberText = songInfo.difficultyNumber.toString().desc(),
                                summaryContent = null,
                            )
                        },
                        buttonText = MR.strings.placement_start.desc(),
                        buttonAction = TrialSessionAction.StartTrial,
                    ),
                    songDetailsBottomSheet = null,
                )
            )
            targetRank.value = rank
        }
    }

    fun handleAction(action: TrialSessionAction) = when (action) {
        is TrialSessionAction.ChangeTargetRank -> {
            targetRank.value = action.target
        }
        TrialSessionAction.StartTrial -> TODO()
        is TrialSessionAction.SubmitFields -> TODO()
        TrialSessionAction.TakePhoto -> TODO()
        is TrialSessionAction.UseShortcut -> TODO()
    }

    private fun getStartingRank(
        playerRank: LadderRank?,
        bestRank: TrialRank?,
        allowedRanks: List<TrialRank>
    ): TrialRank {
        var curr = if (bestRank != null) {
            bestRank.next
        } else {
            TrialRank.fromLadderRank(playerRank, parsePlatinum = true)
        }
        while (curr != null) {
            if (allowedRanks.contains(curr)) {
                return curr
            }
            curr = curr.next
        }
        return bestRank ?: allowedRanks.first()
    }
}

sealed class TrialSessionAction {
    data object StartTrial : TrialSessionAction()
    data class ChangeTargetRank(
        val target: TrialRank
    ) : TrialSessionAction()
    data object TakePhoto : TrialSessionAction()
    data class UseShortcut(
        val songId: String,
        val shortcut: ShortcutType,
    ) : TrialSessionAction()
    data class SubmitFields(
        val items: List<SubmitFieldsItem>
    ) : TrialSessionAction()
}

enum class ShortcutType {
    MFC, PFC, GFC
}

typealias SubmitFieldsItem = Pair<String, String>
