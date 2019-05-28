package com.perrigogames.life4trials.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity
import com.perrigogames.life4trials.util.NotificationUtil.EXTRA_COPY_VALUE

class NotificationCopyHandler: BroadcastReceiver() {

    override fun onReceive(c: Context, intent: Intent) {
        (c.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).let {
            it.primaryClip = ClipData.newPlainText("LIFE4 Data", intent.getStringExtra(EXTRA_COPY_VALUE))
        }
        Toast.makeText(c, c.getString(R.string.copied, intent.getStringExtra(EXTRA_COPY_VALUE)), Toast.LENGTH_SHORT).show()
    }
}

object NotificationUtil {

    const val ID_USER_INFO_CHANNEL = "ID_USER_INFO_CHANNEL"
    const val ID_UPDATES_CHANNEL = "ID_UPDATES_CHANNEL"

    const val ID_NOTIF_RIVAL_CODE = 1001
    const val ID_NOTIF_TWITTER_HANDLE = 1002
    const val ID_NOTIF_EX_SCORE = 1004

    const val ACTION_COPY_CLIPBOARD = "ACTION_COPY_CLIPBOARD"
    const val EXTRA_COPY_VALUE = "EXTRA_COPY_VALUE"

    private fun userInfoNotification(c: Context, @StringRes titleRes: Int, content: String) =
        NotificationCompat.Builder(c, ID_USER_INFO_CHANNEL)
            .setSmallIcon(R.drawable.ic_life4_trials_logo)
            .setContentTitle(c.getString(titleRes))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    fun setupNotifications(c: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = c.getString(R.string.user_info_notifications)
            val descriptionText = c.getString(R.string.user_info_notifications_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ID_USER_INFO_CHANNEL, name, importance).apply {
                description = descriptionText
            }

            (c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    fun showUserInfoNotifications(c: Context, exScore: Int) {
        SharedPrefsUtils.getUserString(c, SettingsActivity.KEY_INFO_RIVAL_CODE)?.let { rivalCode ->
            if (rivalCode.isNotEmpty()) {
                notifyCopyableMessage(c, ID_NOTIF_RIVAL_CODE, R.string.rival_code, rivalCode)
            }
        }
        SharedPrefsUtils.getUserString(c, SettingsActivity.KEY_INFO_TWITTER_NAME)?.let { twitterName ->
            if (twitterName.isNotEmpty()) {
                notifyCopyableMessage(c, ID_NOTIF_TWITTER_HANDLE, R.string.twitter_name, twitterName)
            }
        }
        notifyCopyableMessage(c, ID_NOTIF_EX_SCORE, R.string.ex_score, exScore.toString())
    }

    private fun notifyCopyableMessage(c: Context, id: Int, @StringRes titleRes: Int, message: String) {
        with(NotificationManagerCompat.from(c)) {
            notify(id, userInfoNotification(c, titleRes, c.getString(R.string.tap_to_copy, message))
                .setContentIntent(PendingIntent.getBroadcast(c, id, Intent(c, NotificationCopyHandler::class.java).apply {
                    putExtra(EXTRA_COPY_VALUE, message)
                }, 0))
                .setAutoCancel(true)
                .build()
            )
        }
    }
}