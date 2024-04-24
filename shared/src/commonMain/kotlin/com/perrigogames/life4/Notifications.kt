package com.perrigogames.life4

import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.TrialRank
import com.russhwolf.settings.Settings
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Notifications: KoinComponent {

    protected var multiNotificationId = 2000
        get() = field++

    private val settings: Settings by inject()

    fun showUserInfoNotifications(exScore: Int) {
        settings.getStringOrNull(SettingsKeys.KEY_INFO_RIVAL_CODE)?.let { rivalCode ->
            if (rivalCode.isNotEmpty()) {
                notifyCopyableMessage(ID_NOTIF_RIVAL_CODE, NotificationStrings.rivalCodeTitle, rivalCode)
            }
        }
//        settings.getStringOrNull(SettingsKeys.KEY_INFO_TWITTER_NAME)?.let { twitterName ->
//            if (twitterName.isNotEmpty()) {
//                notifyCopyableMessage(ID_NOTIF_TWITTER_HANDLE, strings.twitterNameTitle, twitterName)
//            }
//        }
        notifyCopyableMessage(ID_NOTIF_EX_SCORE, NotificationStrings.exScoreTitle, exScore.toString())
    }

    /**
     * Sends a generic message to the system's notifications system that will
     * copy
     */
    abstract fun notifyCopyableMessage(id: Int, title: StringDesc, message: String)

    abstract fun showPlacementNotification(rank: LadderRank)

    abstract fun showLadderRankChangedNotification(rank: LadderRank)

    abstract fun showTrialRankChangedNotification(trial: Trial, rank: TrialRank)

    abstract fun showToast(message: String, long: Boolean = false)

    companion object {

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
