package com.perrigogames.life4trials.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.widget.Toast
import com.perrigogames.life4.Notifications.Companion.EXTRA_COPY_VALUE
import com.perrigogames.life4.Notifications.Companion.ID_USER_INFO_CHANNEL
import com.perrigogames.life4trials.R


class NotificationCopyHandler: BroadcastReceiver() {

    override fun onReceive(c: Context, intent: Intent) {
        (c.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            ClipData.newPlainText("LIFE4 Data", intent.getStringExtra(EXTRA_COPY_VALUE)))
        Toast.makeText(c, c.getString(R.string.copied_format, intent.getStringExtra(EXTRA_COPY_VALUE)), Toast.LENGTH_SHORT).show()
    }
}

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
