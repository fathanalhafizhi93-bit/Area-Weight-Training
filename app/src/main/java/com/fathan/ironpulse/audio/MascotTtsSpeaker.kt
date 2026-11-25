package com.fathan.ironpulse.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.MascotPersonality
import java.util.Locale

class MascotTtsSpeaker(context: Context) {

    private var tts: TextToSpeech? = null
    @Volatile
    private var ready: Boolean = false

    init {
        runCatching {
            tts = TextToSpeech(context.applicationContext) { status ->
                ready = status == TextToSpeech.SUCCESS
            }
        }
    }

    fun speak(
        text: String,
        lang: AppLanguage,
        enabled: Boolean,
        personality: MascotPersonality = MascotPersonality.PARTNER
    ) {
        if (!enabled || !ready) return
        val engine = tts ?: return
        val cleaned = text.trim()
        if (cleaned.isEmpty()) return

        runCatching {
            val targetLocale = if (lang == AppLanguage.EN) Locale.US else Locale.forLanguageTag("id-ID")
            val res = engine.setLanguage(targetLocale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                engine.language = Locale.US
            }
            when (personality) {
                MascotPersonality.COACH -> {
                    engine.setPitch(0.86f)
                    engine.setSpeechRate(1.07f)
                }
                MascotPersonality.PARTNER -> {
                    engine.setPitch(1.08f)
                    engine.setSpeechRate(1.02f)
                }
            }
            engine.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, "ironpulse_mascot_line")
        }
    }

    fun stop() {
        runCatching { tts?.stop() }
    }

    fun shutdown() {
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
        tts = null
        ready = false
    }
}
