package com.fathan.ironpulse.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.MascotPersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class MascotTtsSpeaker(context: Context) {

    private var tts: TextToSpeech? = null
    @Volatile
    private var ready: Boolean = false
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeakingFlow: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        runCatching {
            tts = TextToSpeech(context.applicationContext) { status ->
                ready = status == TextToSpeech.SUCCESS
                if (ready) {
                    runCatching {
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                _isSpeaking.value = true
                            }

                            override fun onDone(utteranceId: String?) {
                                _isSpeaking.value = false
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                _isSpeaking.value = false
                            }

                            override fun onError(utteranceId: String?, errorCode: Int) {
                                _isSpeaking.value = false
                            }

                            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                                _isSpeaking.value = false
                            }
                        })
                    }
                }
            }
        }
    }

    fun speak(
        text: String,
        lang: AppLanguage,
        enabled: Boolean,
        personality: MascotPersonality = MascotPersonality.PARTNER
    ) {
        if (!enabled || !ready) {
            _isSpeaking.value = false
            return
        }
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
        }.onFailure {
            _isSpeaking.value = false
        }
    }

    fun stop() {
        _isSpeaking.value = false
        runCatching { tts?.stop() }
    }

    fun shutdown() {
        _isSpeaking.value = false
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
        tts = null
        ready = false
    }
}
