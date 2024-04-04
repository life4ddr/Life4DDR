package com.perrigogames.life4.android.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Html
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perrigogames.life4.MR
import com.perrigogames.life4.Notifications
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.nameRes
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@SuppressLint("MissingPermission")
class AndroidNotifications: Notifications(), KoinComponent {

    val context: Context by inject()

    override fun notifyCopyableMessage(id: Int, title: StringDesc, message: String) {
        with(NotificationManagerCompat.from(context)) {
            notify(id, userInfoNotification(title, context.getString(MR.strings.tap_to_copy.resourceId, message))
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
            val message = context.getString(MR.strings.notification_placement_body.resourceId, context.getString(rank.nameRes.resourceId))
            notify(multiNotificationId, userInfoNotification("WELCOME!")
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showLadderRankChangedNotification(rank: LadderRank) {
        with(NotificationManagerCompat.from(context)) {
            val message = context.getString(
                MR.strings.notification_rank_body.resourceId,
                context.getString(rank.nameRes.resourceId)
            )
            notify(multiNotificationId, userInfoNotification("RANK UP!")
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showTrialRankChangedNotification(trial: Trial, rank: TrialRank) {
        with(NotificationManagerCompat.from(context)) {
            val message = context.getString(
                MR.strings.notification_trial_body.resourceId,
                context.getString(rank.nameRes.resourceId),
                trial.name
            )
            notify(multiNotificationId, userInfoNotification(StringDesc.Raw("RANK UP! ${trial.name}"))
                .setStyle(htmlStyle(message))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, rank.drawableRes))
                .setAutoCancel(true)
                .build())
        }
    }

    override fun showToast(message: String, long: Boolean) {
        Toast.makeText(context, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
    }

    private fun userInfoNotification(title: String, content: String? = null) =
        userInfoNotification(StringDesc.Raw(title))

    private fun userInfoNotification(title: StringDesc, content: String? = null) =
        NotificationCompat.Builder(context, ID_USER_INFO_CHANNEL)
            .setSmallIcon(R.drawable.ic_life4_trials_logo_notif)
            .setContentTitle(title.toString(context))
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
