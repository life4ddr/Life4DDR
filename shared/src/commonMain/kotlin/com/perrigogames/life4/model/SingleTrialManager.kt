package com.perrigogames.life4.model

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.db.SelectBestSession
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.perrigogames.life4.enums.TrialRank
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SingleTrialManager(private val trial: Trial) : BaseModel(), KoinComponent {

    private val dbHelper: TrialDatabaseHelper by inject()

    val rank: TrialRank?
        get() = dbHelper.bestSession(trial.id)?.goalRank

    fun bestSession(): SelectBestSession? = dbHelper.bestSession(trial.id)

    fun saveSession(session: InProgressTrialSession) {
        mainScope.launch {
            dbHelper.insertSession(session)
        }
    }
}