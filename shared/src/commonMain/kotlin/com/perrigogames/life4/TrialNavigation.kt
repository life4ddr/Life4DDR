package com.perrigogames.life4

import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.feature.trials.TrialSessionManager
import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalSerializationApi::class)
abstract class TrialNavigation: KoinComponent {

    private val sessionManager: TrialSessionManager by inject()
    protected val settings: Settings by inject()
    protected val notifications: Notifications by inject()

    open fun submitResult(onFinish: () -> Unit) {
        val session = sessionManager.currentSession!!
        when {
            session.highestPossibleRank == null -> submitRankAndFinish(session, false, onFinish)
            session.trial.isEvent -> submitRankAndFinish(session, true, onFinish)
            else -> showRankConfirmation(session.goalRank!!) { passed ->
                submitRankAndFinish(session, passed, onFinish)
            }
        }
    }

    protected abstract fun showRankConfirmation(rank: TrialRank, result: (Boolean) -> Unit)
    protected abstract fun showSessionSubmitConfirmation(result: (Boolean) -> Unit)
    protected abstract fun showTrialSubmissionWeb()

    private fun submitRankAndFinish(session: InProgressTrialSession, passed: Boolean, onFinish: () -> Unit) {
        session.goalObtained = passed
        sessionManager.saveSession(session)
        if (passed) {
            showSessionSubmitConfirmation { submitOnline ->
                if (submitOnline) {
                    if (settings.getBoolean(SettingsKeys.KEY_SUBMISSION_NOTIFICAION, false)) {
                        notifications.showUserInfoNotifications(session.progress.currentExScore)
                    }
                    showTrialSubmissionWeb()
                }
                onFinish()
            }
        } else {
            onFinish()
        }
    }
}