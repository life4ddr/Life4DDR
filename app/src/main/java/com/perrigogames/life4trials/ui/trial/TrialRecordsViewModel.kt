package com.perrigogames.life4trials.ui.trial

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.perrigogames.life4.db.TrialSession
import com.perrigogames.life4.model.TrialManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class TrialRecordsViewModel : ViewModel(), KoinComponent {

    private val trialManager: TrialManager by inject()

    val records: MutableLiveData<List<TrialSession>> by lazy {
        MutableLiveData<List<TrialSession>>(trialManager.allRecords.reversed())
    }

    fun removeRecord(id: Long) {
        trialManager.deleteSession(id)
        records.value = records.value!!.filterNot { it.id == id }
    }
}
