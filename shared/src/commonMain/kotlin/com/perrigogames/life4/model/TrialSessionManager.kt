package com.perrigogames.life4.model

import com.perrigogames.life4.Notifications
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.TrialNavigation
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import org.koin.core.inject

class TrialSessionManager: BaseModel() {

    private val trialManager: TrialManager by inject()
    private val trialNavigation: TrialNavigation by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBusNotifier by inject()
    private val notifications: Notifications by inject()
    private val dbHelper: TrialDatabaseHelper by inject()

    var currentSession: InProgressTrialSession? = null

    fun startSession(trialId: String, initialGoal: TrialRank?): InProgressTrialSession {
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
                eventBus.post(SavedRankUpdatedEvent(s.trial))
            }
        }
        if (session == currentSession) {
            currentSession = null
        }
    }
}