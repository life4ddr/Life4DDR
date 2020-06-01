package com.perrigogames.life4.model

import com.perrigogames.life4.Notifications
import com.perrigogames.life4.SavedRankUpdatedEvent
import com.perrigogames.life4.SettingsKeys
import com.perrigogames.life4.TrialDialogs
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.db.TrialDatabaseHelper
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import org.koin.core.inject

class TrialSessionManager: BaseModel() {

    private val trialManager: TrialManager by inject()
    private val trialDialogs: TrialDialogs by inject()
    private val settings: Settings by inject()
    private val eventBus: EventBusNotifier by inject()
    private val notifications: Notifications by inject()
    private val dbHelper: TrialDatabaseHelper by inject()

    var currentSession: InProgressTrialSession? = null

    fun startSession(trialId: String, initialGoal: TrialRank?): InProgressTrialSession {
        currentSession = InProgressTrialSession(trialManager.findTrial(trialId)!!, initialGoal)
        return currentSession!!
    }

    fun submitResult(session: InProgressTrialSession = currentSession!!, onFinish: () -> Unit) {
        when {
            session.results.any { it?.passed != true } -> submitRankAndFinish(session, false, onFinish)
            session.trial.isEvent -> submitRankAndFinish(session, true, onFinish)
            else -> trialDialogs.showRankConfirmation(session.goalRank!!) { passed -> submitRankAndFinish(session, passed, onFinish) }
        }
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

    private fun submitRankAndFinish(session: InProgressTrialSession, passed: Boolean, onFinish: () -> Unit) {
        session.goalObtained = passed
        saveSession(session)
        if (passed) {
            trialDialogs.showSessionSubmitConfirmation { submitOnline ->
                if (submitOnline) {
                    if (settings.getBoolean(SettingsKeys.KEY_SUBMISSION_NOTIFICAION, false)) {
                        notifications.showUserInfoNotifications(session.currentTotalExScore)
                    }
                    trialDialogs.showTrialSubmissionWeb()
                }
                onFinish()
            }
        } else {
            onFinish()
        }
    }
}