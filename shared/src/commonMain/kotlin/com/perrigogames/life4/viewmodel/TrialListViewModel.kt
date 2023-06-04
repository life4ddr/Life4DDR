package com.perrigogames.life4.viewmodel

import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_NEW
import com.perrigogames.life4.SettingsKeys.KEY_LIST_HIGHLIGHT_UNPLAYED
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX_REMAINING
import com.perrigogames.life4.SettingsKeys.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4.model.TrialManager
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialListViewModel : ViewModel(), KoinComponent {

    private val trialManager: TrialManager by inject()
    private val settings: Settings by inject()

    private val _trials = MutableLiveData<List<TrialJacketViewModel>>(emptyList())
    val trials: LiveData<List<TrialJacketViewModel>> = _trials

    fun start() {
        val tintCompleted = settings.getBoolean(KEY_LIST_TINT_COMPLETED, true)
        val showEx = settings.getBoolean(KEY_LIST_SHOW_EX, true)
        val showExRemaining = settings.getBoolean(KEY_LIST_SHOW_EX_REMAINING, true)
        val highlightNew = settings.getBoolean(KEY_LIST_HIGHLIGHT_NEW, true)
        val highlightUnplayed = settings.getBoolean(KEY_LIST_HIGHLIGHT_UNPLAYED, true)

        trialManager.trialsFlow.addObserver { trials ->
//            _trials.value = trials.map { trial ->
//                val session = trialManager.bestSession(trial.id)
//                TrialJacketViewModel(
//                    trial = trial,
//                    session = session,
//                    rank = session?.goalRank,
//                    exScore = session?.exScore?.toInt(),
//                    tintOnRank = TrialRank.values().last(),
//                    showExRemaining = ,
//                )
//            }
        }
    }
}