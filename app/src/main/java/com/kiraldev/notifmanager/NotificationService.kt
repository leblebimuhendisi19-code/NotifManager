package com.kiraldev.notifmanager

import android.app.NotificationManager
import android.media.AudioManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    private val prefs by lazy { getSharedPreferences("notif_prefs", MODE_PRIVATE) }
    private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName
        val mode = prefs.getString(pkg, "default") ?: "default"

        when (mode) {
            "silent" -> cancelNotification(sbn.key)
            "sound"  -> ensureSound()
        }
    }

    private fun ensureSound() {
        val currentMode = audioManager.ringerMode
        if (currentMode != AudioManager.RINGER_MODE_NORMAL) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            android.os.Handler(mainLooper).postDelayed({
                audioManager.ringerMode = currentMode
            }, 3000)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}
}
