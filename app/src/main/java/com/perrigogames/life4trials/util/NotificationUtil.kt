package com.perrigogames.life4trials.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.activity.SettingsActivity

object NotificationUtil {

    const val ID_USER_INFO_CHANNEL = "ID_USER_INFO_CHANNEL"
    const val ID_UPDATES_CHANNEL = "ID_UPDATES_CHANNEL"

    const val ID_NOTIF_RIVAL_CODE = 1001
    const val ID_NOTIF_TWITTER_HANDLE = 1002
    const val ID_NOTIF_TRIAL_NAME = 1003
    const val ID_NOTIF_EX_SCORE = 1004

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
        with(NotificationManagerCompat.from(c)) {
            SharedPrefsUtils.getUserString(c, SettingsActivity.KEY_INFO_RIVAL_CODE)?.let { rivalCode ->
                if (rivalCode.isNotEmpty()) {
                    notify(ID_NOTIF_RIVAL_CODE, userInfoNotification(c, R.string.rival_code, rivalCode).build())
                }
            }
            SharedPrefsUtils.getUserString(c, SettingsActivity.KEY_INFO_TWITTER_NAME)?.let { twitterName ->
                if (twitterName.isNotEmpty()) {
                    notify(ID_NOTIF_TWITTER_HANDLE, userInfoNotification(c, R.string.twitter_name, twitterName).build())
                }
            }
            notify(ID_NOTIF_EX_SCORE, userInfoNotification(c, R.string.ex_score, exScore.toString()).build())
        }
    }
}