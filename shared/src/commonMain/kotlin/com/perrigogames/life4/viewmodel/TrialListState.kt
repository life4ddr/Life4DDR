package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialState
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.enums.TrialJacketCorner

/**
 * A View state describing the Trial list and its contents
 */
data class TrialListState(
    private val trials: List<Trial>,
    private val sessions: List<SelectBestSessions>,
    val featureNew: Boolean = false,
) {

    private val matchedTrials = trials.associateWith { trial ->
        sessions.firstOrNull { it.trialId == trial.id }
    }

    private fun trialItem(trial: Trial) = TrialListItem.Trial(
        trial = trial,
        session = matchedTrials[trial],
        corner = when {
            trial.isActiveEvent -> TrialJacketCorner.EVENT
            featureNew && trial.new && matchedTrials[trial] == null -> TrialJacketCorner.NEW
            else -> null
        }
    )

    val displayTrials: List<TrialListItem> by lazy {
        val event = mutableListOf<TrialListItem.Trial>()
        val new = mutableListOf<TrialListItem.Trial>()
        val active = mutableListOf<TrialListItem.Trial>()
        val retired = mutableListOf<TrialListItem.Trial>()

        trials.map { trialItem(it) }
            .forEach { item ->
                when {
                    item.corner == TrialJacketCorner.EVENT -> event
                    item.corner == TrialJacketCorner.NEW -> new
                    item.trial.state == TrialState.RETIRED -> retired
                    else -> active
                }.add(item)
            }

        //FIXME i18n
        mutableListOf<TrialListItem>(
            TrialListItem.Header("Active Trials")
        ).apply {
            addAll(event)
            addAll(new)
            addAll(active)
            add(TrialListItem.Header("Retired Trials"))
            addAll(retired)
        }
    }

    sealed class TrialListItem {

        class Trial(
            val trial: com.perrigogames.life4.data.Trial,
            val session: SelectBestSessions?,
            val corner: TrialJacketCorner?,
        ) : TrialListItem()
        class Header(val text: String) : TrialListItem()
    }
}