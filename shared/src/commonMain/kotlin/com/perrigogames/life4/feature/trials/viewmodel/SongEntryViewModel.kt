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
    private val index: Int,
    shortcut: ShortcutType? = null,
    private val isEdit: Boolean,
): ViewModel(), KoinComponent {

    private val settingsManager: SettingsManager by inject()

    val passedChecked: CMutableStateFlow<Boolean> = MutableStateFlow(true).cMutableStateFlow()
    val result get() = session.results[index]!!

    private val _numberMap = MutableStateFlow(mapOf(
        ID_SCORE to result.score,
        ID_EX_SCORE to result.exScore,
        ID_MISSES to result.misses,
        ID_GOODS to result.goods,
        ID_GREATS to result.greats,
        ID_PERFECTS to result.perfects,
        ID_PASSED to if (result.passed) 1 else 0,
    ))
    private val _viewDataMap = MutableStateFlow(mapOf(
        ID_SCORE to UITrialBottomSheet.Field(
            id = ID_SCORE,
            text = "",
            weight = 2f,
            label = MR.strings.score.desc()
        ),
        ID_EX_SCORE to UITrialBottomSheet.Field(
            id = ID_EX_SCORE,
            text = "",
            label = MR.strings.ex_score.desc()
        ),
        ID_MISSES to UITrialBottomSheet.Field(
            id = ID_MISSES,
            text = "",
            label = MR.strings.misses.desc()
        ),
        ID_GOODS to UITrialBottomSheet.Field(
            id = ID_GOODS,
            text = "",
            label = MR.strings.goods.desc()
        ),
        ID_GREATS to UITrialBottomSheet.Field(
            id = ID_GREATS,
            text = "",
            label = MR.strings.greats.desc()
        ),
        ID_PERFECTS to UITrialBottomSheet.Field(
            id = ID_PERFECTS,
            text = "",
            label = MR.strings.perfects.desc()
        ),
    ))

    private val _state = MutableStateFlow(generateViewState(shortcut)).cMutableStateFlow()
    val state: CStateFlow<UITrialBottomSheet.Details> = _state.cStateFlow()

    private fun generateViewState(
        shortcut: ShortcutType?
    ): UITrialBottomSheet.Details {
        // TODO process shortcut
        val requiredFields = requiredFields(session.trial, targetRank)
        return UITrialBottomSheet.Details(
            imagePath = session.results[index]?.photoUriString.orEmpty(),
            fields = requiredFields.fold(mutableListOf<MutableList<UITrialBottomSheet.Field>>()) { acc, id ->
                if (id == NEWLINE) {
                    acc.add(mutableListOf())
                } else {
                    val number = _numberMap.value[id]
                    if (acc.isEmpty()) acc.add(mutableListOf())
                    acc.last().add(
                        _viewDataMap.value[id]!!.copy(
                            text = if (number != null && number != 0) number.toString() else "",
                        )
                    )
                }
                acc
            },
            isEdit = isEdit,
            shortcuts = emptyList(), // FIXME
        )
    }

    fun setShortcutState(shortcut: ShortcutType?) {
        _state.value = generateViewState(shortcut)
    }

    fun changeText(id: String, text: String) {
        _numberMap.update { map ->
            val number = text.toIntOrNull()
            map + (id to number)
        }
        _state.update { state ->
            state.copy(
                fields = state.fields.map { row ->
                    row.map { field ->
                        if (field.id == id) {
                            field.copy(text = text)
                        } else {
                            field
                        }
                    }
                }
            )
        }
    }

    fun commitChanges(): InProgressTrialSession {
        val result = result.copy(
            score = _numberMap.value[ID_SCORE],
            exScore = _numberMap.value[ID_EX_SCORE],
            misses = _numberMap.value[ID_MISSES],
            goods = _numberMap.value[ID_GOODS],
            greats = _numberMap.value[ID_GREATS],
            perfects = _numberMap.value[ID_PERFECTS],
            passed = passedChecked.value,
        )
        return session.copy(
            results = session.results.copyOf().also {
                it[index] = result
            }
        )
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
            out += ID_GREATS
            out += ID_GOODS
        }
        if (goalTypes.contains(MISS) || goalTypes.contains(BAD_JUDGEMENT)) {
            out += ID_MISSES
            val newlineIndex = out.indexOfFirst { it == ID_EX_SCORE } + 1
            out.add(newlineIndex, NEWLINE)
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
        const val NEWLINE = "newline"
    }
}
