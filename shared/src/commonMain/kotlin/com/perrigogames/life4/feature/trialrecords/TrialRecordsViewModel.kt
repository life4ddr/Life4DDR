package com.perrigogames.life4.feature.trialrecords

import com.perrigogames.life4.data.trialrecords.UITrialRecord
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialRecordsViewModel : ViewModel(), KoinComponent {
    private val recordsManager: TrialRecordsManager by inject()

    private val _records = MutableStateFlow<List<UITrialRecord>>(emptyList()) // FIXME
    val records: CStateFlow<List<UITrialRecord>> = _records.cStateFlow()

    fun removeRecord(id: Long) {
        recordsManager.deleteSession(id)
    }
}
