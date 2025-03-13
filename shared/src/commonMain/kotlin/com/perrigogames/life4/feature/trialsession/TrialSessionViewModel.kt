package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
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

    private val trial = trialManager.trialsFlow.value.firstOrNull { it.id == trialId }
        ?: throw IllegalStateException("Can't find trial with id $trialId")

    private val contentProvider = TrialContentProvider(trial = trial)

    private val targetRank = MutableStateFlow(TrialRank.BRONZE)
    private val _state = MutableStateFlow<ViewState<UITrialSession, Unit>>(ViewState.Loading)
        .cMutableStateFlow()
    val state: StateFlow<ViewState<UITrialSession, Unit>> = _state.asStateFlow()

    private val stage = MutableStateFlow<Int?>(null)
    private val inProgressSessionFlow = MutableStateFlow<InProgressTrialSession?>(null)


    init {
        viewModelScope.launch { // recreate the in progress session when necessary
            targetRank.map { targetRank ->
                InProgressTrialSession(
                    trial = trial,
                    targetRank = targetRank,
                )
            }.collect(inProgressSessionFlow)
        }

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

        viewModelScope.launch {
            combine(
                inProgressSessionFlow.filterNotNull(),
                stage.filterNotNull(),
            ) { session, stage ->
                val current = (_state.value as? ViewState.Success)?.data ?: return@combine
                _state.value = current.copy(
                    targetRank = when (val target = current.targetRank) {
                        is UITargetRank.Selection -> target.toInProgress()
                        is UITargetRank.InProgress -> target
                        is UITargetRank.Achieved -> throw IllegalStateException("Can't move from Achieved to In Progress")
                    },
                    content = contentProvider.provideMidSession(session, stage)
                ).toViewState()
            }.collect()
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
                    content = contentProvider.provideSummary(),
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
        TrialSessionAction.StartTrial -> {
            stage.value = 0
        }
        is TrialSessionAction.SubmitFields -> TODO()
        TrialSessionAction.TakePhoto -> TODO()
        is TrialSessionAction.AdvanceStage -> {
            inProgressSessionFlow.value?.let { session ->
                inProgressSessionFlow.value = session.copy()
            }
            stage.value = (stage.value ?: 0) + 1
        }
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

    data class AdvanceStage(
        val photoUri: String
    ) : TrialSessionAction()

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
