package com.perrigogames.life4.feature.trials.viewmodel

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.SongResult
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.manager.TrialDataManager
import com.perrigogames.life4.feature.trials.manager.TrialRecordsManager
import com.perrigogames.life4.feature.trials.provider.TrialBottomSheetProvider
import com.perrigogames.life4.feature.trials.provider.TrialContentProvider
import com.perrigogames.life4.feature.trials.provider.TrialGoalStrings
import com.perrigogames.life4.feature.trials.view.*
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

    private val userRankManager: UserRankManager by inject()
    private val trialDataManager: TrialDataManager by inject()
    private val trialRecordsManager: TrialRecordsManager by inject()

    private val trial = trialDataManager.trialsFlow.value.firstOrNull { it.id == trialId }
        ?: throw IllegalStateException("Can't find trial with id $trialId")

    private val contentProvider = TrialContentProvider(trial = trial)

    private val targetRank = MutableStateFlow(TrialRank.BRONZE)
    private val _state = MutableStateFlow<ViewState<UITrialSession, Unit>>(ViewState.Loading)
        .cMutableStateFlow()
    val state: StateFlow<ViewState<UITrialSession, Unit>> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TrialSessionEvent>()
    val events: SharedFlow<TrialSessionEvent> = _events.asSharedFlow()

    private val stage = MutableStateFlow<Int?>(null)
    private val inProgressSessionFlow = MutableStateFlow<InProgressTrialSession?>(null)
    private val inProgressSession get() = inProgressSessionFlow.value!!

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
                val trial = trialDataManager.trialsFlow.value
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
                val complete = stage >= 4
                val current = (_state.value as? ViewState.Success)?.data ?: return@combine
                val targetRank = when (val target = current.targetRank) {
                    is UITargetRank.Selection -> target.toInProgress()
                    is UITargetRank.InProgress -> target
                    is UITargetRank.Achieved -> throw IllegalStateException("Can't move from Achieved to In Progress")
                }
                _state.value = if (complete) {
                    current.copy(
                        targetRank = targetRank.toAchieved(), // FIXME calculate the user's actual rank
                        content = contentProvider.provideFinalScreen(session),
                        buttonText = MR.strings.take_results_photo.desc(),
                        buttonAction = TrialSessionAction.TakeResultsPhoto,
                    )
                } else {
                    current.copy(
                        targetRank = targetRank,
                        content = contentProvider.provideMidSession(session, stage),
                        buttonText = MR.strings.take_photo.desc(),
                        buttonAction = TrialSessionAction.TakePhoto(stage),
                    )
                }.toViewState()
            }.collect()
        }

        val trial = trialDataManager.trialsFlow.value
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
                    buttonText = MR.strings.placement_start.desc(),
                    buttonAction = TrialSessionAction.StartTrial,
                )
            )
            targetRank.value = rank
        }
    }

    fun handleAction(action: TrialSessionAction) {
        when (action) {
            is TrialSessionAction.ChangeTargetRank -> {
                targetRank.value = action.target
            }

            TrialSessionAction.StartTrial -> {
                stage.value = 0
            }

            is TrialSessionAction.TakePhoto -> {
                viewModelScope.launch {
                    _events.emit(TrialSessionEvent.AcquirePhoto(action.index))
                }
            }

            is TrialSessionAction.TakeResultsPhoto -> {
                viewModelScope.launch {
                    _events.emit(TrialSessionEvent.AcquireResultsPhoto)
                }
            }

            is TrialSessionAction.PhotoTaken -> {
                val song = inProgressSession.trial.songs[action.index]
                val result = inProgressSession.results[action.index] ?: SongResult(song).also {
                    inProgressSession.results[action.index] = it
                }
                result.photoUriString = action.photoUri
                viewModelScope.launch {
                    val bottomSheet = TrialBottomSheetProvider.provide(
                        songResult = result,
                        songId = song.skillId,
                        imagePath = action.photoUri,
                        shortcut = null,
                    )
                    _events.emit(TrialSessionEvent.ShowBottomSheet(bottomSheet))
                }
            }

            is TrialSessionAction.ResultsPhotoTaken -> {
                // TODO acquire the images and upload them to the API
                inProgressSession.goalObtained = true
                trialRecordsManager.saveSession(inProgressSession)
                viewModelScope.launch {
                    _events.emit(TrialSessionEvent.Close)
                }
            }

            is TrialSessionAction.EditItem -> {
                val song = inProgressSession.trial.songs[action.index]
                val result = inProgressSession.results[action.index] ?: SongResult(song).also {
                    inProgressSession.results[action.index] = it
                }
                viewModelScope.launch {
                    val bottomSheet = TrialBottomSheetProvider.provide(
                        songResult = result,
                        songId = song.skillId,
                        imagePath = result.photoUriString ?: "",
                        shortcut = null,
                    )
                    _events.emit(TrialSessionEvent.ShowBottomSheet(bottomSheet))
                }
            }

            is TrialSessionAction.SubmitFields -> TODO()

            is TrialSessionAction.AdvanceStage -> {
                inProgressSessionFlow.value?.let { session ->
                    inProgressSessionFlow.value = session.copy()
                }
                stage.value = (stage.value ?: 0) + 1
            }

            is TrialSessionAction.UseShortcut -> TODO()
        }
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

enum class ShortcutType {
    MFC, PFC, GFC
}

typealias SubmitFieldsItem = Pair<String, String>
