package com.example.careconnect.platform

// Lokale Benachrichtigung, Android: NotificationManager, iOS: UNUserNotificationCenter
interface Notifier {
    fun show(id: String, title: String, text: String)
}
