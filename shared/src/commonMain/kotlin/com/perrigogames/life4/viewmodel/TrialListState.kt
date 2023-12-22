package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialState
import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.enums.TrialJacketCorner
import com.perrigogames.life4.enums.TrialRank

/**
 * A View state describing the Trial list and its contents
 */
data class TrialListState(
    val trials: List<Trial>,
    private val sessions: List<SelectBestSessions>,
    val featureNew: Boolean = false,
    val featureUnplayed: Boolean = false,
) {

    private val matchedTrials = trials.associateWith { trial ->
        sessions.firstOrNull { it.trialId == trial.id }
    }

    private fun trialViewModel(trial: Trial) = UITrialJacket(
        trial = trial,
        session = matchedTrials[trial],
        rank = matchedTrials[trial]?.goalRank,
        exScore = matchedTrials[trial]?.exScore?.toInt(),
        tintOnRank = TrialRank.values().last(),
        showExRemaining = false,
    )

    val displayTrials: List<Item> by lazy {
        val retired = mutableListOf<Item.Trial>()
        val event = mutableListOf<Item.Trial>()
        val new = mutableListOf<Item.Trial>()
        val unplayed = mutableListOf<Item.Trial>()
        val active = mutableListOf<Item.Trial>()

        trials.map { trialViewModel(it) }
            .forEach { item ->
                when {
                    item.trial.state == TrialState.RETIRED -> retired
                    item.cornerType == TrialJacketCorner.EVENT -> event
                    featureNew && item.cornerType == TrialJacketCorner.NEW -> new
                    featureUnplayed && item.session == null -> unplayed
                    else -> active
                }.add(Item.Trial(item))
            }

        //FIXME i18n
        mutableListOf<Item>(
            Item.Header("Active Trials")
        ).apply {
            addAll(event)
            addAll(new)
            addAll(unplayed)
            addAll(active)
            add(Item.Header("Retired Trials"))
            addAll(retired)
        }
    }

    sealed class Item {

        class Trial(
            val viewModel: UITrialJacket,
        ) : Item() {

            override fun toString() = "Trial: ${viewModel.trial.name}"
        }

        class Header(val text: String) : Item() {
            override fun toString() = "Header: $text"
        }
    }
}