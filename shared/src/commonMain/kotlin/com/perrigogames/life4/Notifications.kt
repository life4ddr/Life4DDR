package com.perrigogames.life4

import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialRank
import com.russhwolf.settings.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class Notifications: KoinComponent {

    private val settings: Settings by inject()
    private val platStrings: PlatformStrings by inject()
    private val strings: NotificationStrings get() = platStrings.notification

    fun showUserInfoNotifications(exScore: Int) {
        settings.getStringOrNull(SettingsKeys.KEY_INFO_RIVAL_CODE)?.let { rivalCode ->
            if (rivalCode.isNotEmpty()) {
                notifyCopyableMessage(ID_NOTIF_RIVAL_CODE, strings.rivalCodeTitle, rivalCode)
            }
        }
        settings.getStringOrNull(SettingsKeys.KEY_INFO_TWITTER_NAME)?.let { twitterName ->
            if (twitterName.isNotEmpty()) {
                notifyCopyableMessage(ID_NOTIF_TWITTER_HANDLE, strings.twitterNameTitle, twitterName)
            }
        }
        notifyCopyableMessage(ID_NOTIF_EX_SCORE, strings.exScoreTitle, exScore.toString())
    }

    /**
     * Sends a generic message to the system's notifications system that will
     * copy
     */
    abstract fun notifyCopyableMessage(id: Int, title: String, message: String)

    abstract fun showPlacementNotification(rank: LadderRank)

    abstract fun showLadderRankChangedNotification(rank: LadderRank)

    abstract fun showTrialRankChangedNotification(trial: Trial, rank: TrialRank)

    abstract fun showToast(message: String, long: Boolean = false)

    companion object {
        var MULTI_NOTIFICATION_ID = 2000
            get() = field++

        const val ID_USER_INFO_CHANNEL = "ID_USER_INFO_CHANNEL"
        const val ID_UPDATES_CHANNEL = "ID_UPDATES_CHANNEL"

        const val ID_NOTIF_RIVAL_CODE = 1001
        const val ID_NOTIF_TWITTER_HANDLE = 1002
        const val ID_NOTIF_EX_SCORE = 1004
        const val ID_NOTIF_TRIAL_RANK = 1005

        const val ACTION_COPY_CLIPBOARD = "ACTION_COPY_CLIPBOARD"
        const val EXTRA_COPY_VALUE = "EXTRA_COPY_VALUE"
    }
}
