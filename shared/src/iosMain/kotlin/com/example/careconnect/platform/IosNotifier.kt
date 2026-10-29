package com.example.careconnect.platform

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

// Zeigt lokale Benachrichtigungen auf iOS
class IosNotifier : Notifier {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    // delegate ist weak, deshalb hier festhalten
    private val delegate = ForegroundDelegate()

    init {
        center.delegate = delegate
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ -> }
    }

    override fun show(id: String, title: String, text: String) {
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(text)
        content.setSound(UNNotificationSound.defaultSound)

        val request = UNNotificationRequest.requestWithIdentifier(id, content, null)
        center.addNotificationRequest(request, null)
    }
}

// Sonst zeigt iOS nichts an, solange die App offen ist
private class ForegroundDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        withCompletionHandler(
            UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionList or UNNotificationPresentationOptionSound
        )
    }
}
