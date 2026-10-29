package com.example.careconnect.platform

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.careconnect.i18n.tr

// Zeigt lokale Benachrichtigungen auf Android
class AndroidNotifier(
    private val context: Context
) : Notifier {

    private val manager = context.getSystemService(NotificationManager::class.java)

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            tr("Aufträge"),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = tr("Neue Aufträge, Verordnungen und Änderungen")
        }
        manager.createNotificationChannel(channel)
    }

    override fun show(id: String, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        // Tippen öffnet die App
        val openApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.let { intent ->
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon())
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setContentIntent(openApp)
            .build()

        manager.notify(id.hashCode(), notification)
    }

    // Icon liegt in androidApp/res/drawable
    private fun smallIcon(): Int {
        val id = context.resources.getIdentifier("ic_notification", "drawable", context.packageName)
        return if (id != 0) id else android.R.drawable.ic_dialog_info
    }

    private companion object {
        const val CHANNEL_ID = "careconnect_orders"
    }
}
