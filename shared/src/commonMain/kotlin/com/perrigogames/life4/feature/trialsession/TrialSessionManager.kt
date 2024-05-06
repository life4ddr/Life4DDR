package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.feature.trialrecords.TrialDatabaseHelper
import com.perrigogames.life4.feature.trials.TrialManager
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialSessionManager : BaseModel(), KoinComponent {
    private val trialManager: TrialManager by inject()
    private val dbHelper: TrialDatabaseHelper by inject()

    var currentSession: InProgressTrialSession? = null

    fun startSession(
        trialId: String,
        initialGoal: TrialRank?,
    ): InProgressTrialSession {
        currentSession = InProgressTrialSession(trialManager.findTrial(trialId)!!, initialGoal)
        return currentSession!!
    }

    /**
     * Commits the current session to internal storage.  [currentSession] is
     * no longer usable after calling this.
     */
    fun saveSession(session: InProgressTrialSession? = currentSession) {
        session?.let { s ->
            mainScope.launch {
                dbHelper.insertSession(s)
                // FIXME eventBus.post(SavedRankUpdatedEvent(s.trial))
            }
        }
        if (session == currentSession) {
            currentSession = null
        }
    }
}
