package com.fathan.ironpulse

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.fathan.ironpulse.data.GymPresets
import com.fathan.ironpulse.ui.components.ScaledBackdropCache
import com.fathan.ironpulse.ui.theme.ThemeMaterialShaderCache
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class IronPulseApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        runCatching {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        }
        runCatching { createNotificationChannels() }
        appScope.launch {
            runCatching {
                ScaledBackdropCache.preload(applicationContext, R.drawable.gym_arena_backdrop, 1024)
            }
        }
        appScope.launch(Dispatchers.Default) {
            runCatching {
                GymPresets.EXERCISE_SEARCH_INDEX.size
                val densityScale = resources.displayMetrics.density.toInt().coerceIn(1, 3)
                ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = 6, subtle = true)
                ThemeMaterialShaderCache.getYaruMatteGrainBrush(pixelScale = densityScale, subtle = true)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return

        val alarmSoundUri = Uri.parse("android.resource://$packageName/${R.raw.ironpulse_alarm}")
        val alarmAudioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val alarmChannel = NotificationChannel(
            CHANNEL_GYM_ALARM,
            "Alarm Latihan Gym",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi prioritas tinggi saat waktu jadwal latihan tiba"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 450, 220, 450, 220, 600)
            setSound(alarmSoundUri, alarmAudioAttrs)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }

        val restTimerChannel = NotificationChannel(
            CHANNEL_REST_TIMER,
            "Rest Timer Aktif",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Menampilkan hitungan mundur waktu istirahat antar set"
            setShowBadge(false)
        }

        val restAlertChannel = NotificationChannel(
            CHANNEL_REST_DONE,
            "Istirahat Selesai",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Bunyi & getaran saat waktu istirahat antar set berakhir"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 120, 250)
        }

        manager.createNotificationChannels(listOf(alarmChannel, restTimerChannel, restAlertChannel))
    }

    companion object {
        const val CHANNEL_GYM_ALARM = "ironpulse_gym_alarm_v1"
        const val CHANNEL_REST_TIMER = "ironpulse_rest_timer_v1"
        const val CHANNEL_REST_DONE = "ironpulse_rest_done_v1"
    }
}
