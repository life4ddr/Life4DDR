package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.TrialStrings
import com.perrigogames.life4.data.*
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.model.LadderManager
import com.perrigogames.life4.model.SingleTrialManager
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalSerializationApi::class)
class TrialDetailsViewModel(
    val trial: Trial,
    initialRankOverride: TrialRank? = null,
) : ViewModel(), KoinComponent {

    /// region Dependencies

    val ladderManager: LadderManager by inject()
    val strings: PlatformStrings by inject()

    val trialManager = SingleTrialManager(trial)

    /// endregion

    /// region Ranks

    private val storedRank: TrialRank? = trialManager.rank

    private val initialRank: TrialRank =
        if (trial.isEvent)
            TrialRank.fromLadderRank(ladderManager.currentRank, true) ?:
            TrialRank.COPPER
        else
            storedRank?.let { trial.rankAfter(it) } ?:
            initialRankOverride ?:
            TrialRank.fromLadderRank(ladderManager.currentRank, false) ?:
            TrialRank.COPPER

    private val _targetRank = MutableStateFlow(trial.toTargetRankView(initialRank, strings.trial)).cMutableStateFlow()
    val targetRankView: StateFlow<TargetRankView> = _targetRank

    fun setTargetRank(rank: TrialRank) {
        viewModelScope.launch {
            _targetRank.emit(trial.toTargetRankView(rank, strings.trial))
            _session.emit(_session.value.copy(
                goalRank = rank
            ))
        }
    }

    /// endregion

    /// Session Progress

    private val _session = MutableStateFlow(InProgressTrialSession(trial)).cMutableStateFlow()
    val session: StateFlow<InProgressTrialSession> = _session

    val exProgress: StateFlow<TrialEXProgress> = session.map { it.progress }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TrialEXProgress(0, 0, 0)
        )

    val highestPossibleRank = session.map { it.highestPossibleRank }

    val songViews = session.map { session ->
        trial.songs.map { song ->
            TrialSongView(
                song = song,
                result = session.results.firstOrNull { it?.song == song }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun saveSession() {
        trialManager.saveSession(session.value)
    }

    /// endregion

    private val _filesystemChecked = MutableStateFlow(false).cMutableStateFlow()
    val filesystemChecked: StateFlow<Boolean> = _filesystemChecked
    fun setFilesystemChecked(checked: Boolean) {
        viewModelScope.launch {
            _filesystemChecked.emit(checked)
        }
    }
}

data class TargetRankView(
    val rank: TrialRank,
    val availableRanks: List<TrialRank>,
    val goalText: String,
)

data class TrialSongView(
    val song: Song,
    val result: SongResult?,
) {
    val title get() = song.name
    val difficultyClass get() = song.difficultyClass
    val difficultyNumber get() = song.difficultyNumber
    val jacketUrl get() = song.url
    val hasResult get() = result != null
    val resultText get() = "${result?.score} (${result?.exScore} EX)"
    val resultBold get() = false // FIXME
    val resultTextColor: Color? get() = null // FIXME
}

fun Trial.toTargetRankView(targetRank: TrialRank, strings: TrialStrings) = TargetRankView(
    rank = targetRank,
    availableRanks = goals?.map { it.rank } ?: emptyList(),
    goalText = goals!!.first {
        it.rank == targetRank
    }.generateSingleGoalString(
        s = strings,
        trial = this,
    )
)