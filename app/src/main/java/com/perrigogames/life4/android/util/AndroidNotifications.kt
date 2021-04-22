package com.perrigogames.life4.android.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Html
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perrigogames.life4.Notifications
import com.perrigogames.life4.data.LadderRank
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialRank
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.android.nameRes
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidNotifications: Notifications(), KoinComponent {

    val context: Context by inject()

    override fun notifyCopyableMessage(id: Int, title: String, message: String) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, userInfoNotification(title, context.getString(R.string.tap_to_copy, message))
                .setContentIntent(PendingIntent.getBroadcast(context, id,
                    Intent(context, NotificationCopyHandler::class.java).apply {
                        putExtra(EXTRA_COPY_VALUE, message)
                    }, 0))
                .setAutoCancel(true)
                .build()
            )
        }
    }

    override fun showPlacementNotification(rank: LadderRank) {
        with(NotificationManagerCompat.from(context)) {
            val message = context.getString(R.string.notification_placement_body, context.getString(rank.nameRes))
            notify(MULTI_NOTIFICATION_ID, userInfoNotification("WELCOME!")
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showLadderRankChangedNotification(rank: LadderRank) {
        with(NotificationManagerCompat.from(context)) {
            val message = context.getString(R.string.notification_rank_body, context.getString(rank.nameRes))
            notify(MULTI_NOTIFICATION_ID, userInfoNotification("RANK UP!")
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showTrialRankChangedNotification(trial: Trial, rank: TrialRank) {
        with(NotificationManagerCompat.from(context)) {
            val message = context.getString(R.string.notification_trial_body, context.getString(rank.nameRes), trial.name)
            notify(MULTI_NOTIFICATION_ID, userInfoNotification("RANK UP! ${trial.name}")
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showToast(message: String, long: Boolean) {
        Toast.makeText(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
    }

    private fun userInfoNotification(@StringRes titleRes: Int, content: String) =
        userInfoNotification(context.getString(titleRes), content)

    private fun userInfoNotification(title: String, content: String? = null) =
        NotificationCompat.Builder(context, ID_USER_INFO_CHANNEL)
            .setSmallIcon(R.drawable.ic_life4_trials_logo_notif)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply {
                content?.let { setContentText(it) }
            }

    private fun htmlStyle(message: String) = NotificationCompat.InboxStyle().also {
        @Suppress("DEPRECATION")
        it.addLine(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(message, 0) else
            Html.fromHtml(message))
    }
}
