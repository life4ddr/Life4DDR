package com.perrigogames.life4.feature.trials.viewmodel

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.feature.settings.SettingsManager
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.data.TrialGoalSet.GoalType.*
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.view.UITrialBottomSheet
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongEntryViewModel(
    private val session: InProgressTrialSession,
    private val targetRank: TrialRank,
): ViewModel(), KoinComponent {

    private val settingsManager: SettingsManager by inject()

    val passedChecked: CMutableStateFlow<Boolean> = MutableStateFlow(true).cMutableStateFlow()

    private val _numberMap = MutableStateFlow(mapOf(
        ID_SCORE to 0,
        ID_EX_SCORE to 0,
        ID_MISSES to 0,
        ID_GOODS to 0,
        ID_GREATS to 0,
        ID_PERFECTS to 0,
        ID_PASSED to 0,
    ))
    private val _viewDataMap = MutableStateFlow(mapOf(
        ID_SCORE to UITrialBottomSheet.Field(
            id = ID_SCORE,
            text = "",
            weight = 2f,
            placeholder = MR.strings.score.desc()
        ),
        ID_EX_SCORE to UITrialBottomSheet.Field(
            id = ID_EX_SCORE,
            text = "",
            placeholder = MR.strings.ex_score.desc()
        ),
        ID_MISSES to UITrialBottomSheet.Field(
            id = ID_MISSES,
            text = "",
            placeholder = MR.strings.misses.desc()
        ),
        ID_GOODS to UITrialBottomSheet.Field(
            id = ID_GOODS,
            text = "",
            placeholder = MR.strings.goods.desc()
        ),
        ID_GREATS to UITrialBottomSheet.Field(
            id = ID_GREATS,
            text = "",
            placeholder = MR.strings.greats.desc()
        ),
        ID_PERFECTS to UITrialBottomSheet.Field(
            id = ID_PERFECTS,
            text = "",
            placeholder = MR.strings.perfects.desc()
        ),
    ))

    private val _state = MutableStateFlow(
        UITrialBottomSheet.Details(
            imagePath = "FIXME",
            fields = emptyList(),
            shortcuts = emptyList()
        )
    ).cMutableStateFlow()
    val state: CStateFlow<UITrialBottomSheet.Details> = _state.cStateFlow()

    fun updateViewState(
        index: Int,
        shortcut: ShortcutType? = null,
    ) {
        _state.value = generateViewState(
            session,
            targetRank,
            index,
            shortcut
        )
    }

    fun generateViewState(
        session: InProgressTrialSession,
        targetRank: TrialRank,
        index: Int,
        shortcut: ShortcutType?,
    ): UITrialBottomSheet.Details {
        // TODO process shortcut
        val requiredFields = requiredFields(session.trial, targetRank)
        return UITrialBottomSheet.Details(
            imagePath = session.results[index]?.photoUriString.orEmpty(),
            fields = requiredFields.map { id ->
                _viewDataMap.value[id]!!.copy(
                    text = _numberMap.value[id].toString(),
                )
            },
            shortcuts = emptyList(), // FIXME
        )
    }

    fun changeText(id: String, text: String) {
        _state.update { state ->
            state.copy(
                fields = state.fields.map {
                    if (it.id == id) {
                        it.copy(text = text)
                    } else {
                        it
                    }
                }
            )
        }
    }

    private fun requiredFields(trial: Trial, targetRank: TrialRank): List<String> {
        val goalTypes = trial.goalSet(targetRank)
            ?.goalTypes
            ?.toMutableList()
            ?: return emptyList()

        val out = mutableListOf<String>()
        if (goalTypes.contains(SCORE)) {
            out += ID_SCORE
        }
        out += ID_EX_SCORE
        if (goalTypes.contains(BAD_JUDGEMENT)) {
            out += ID_GOODS
            out += ID_GREATS
        }
        if (goalTypes.contains(MISS) || goalTypes.contains(BAD_JUDGEMENT)) {
            out += ID_MISSES
        }
        return out.toList()
    }

    companion object {
        const val ID_SCORE = "score"
        const val ID_EX_SCORE = "ex_score"
        const val ID_MISSES = "misses"
        const val ID_GOODS = "goods"
        const val ID_GREATS = "greats"
        const val ID_PERFECTS = "perfects"
        const val ID_PASSED = "passed"
    }
}
