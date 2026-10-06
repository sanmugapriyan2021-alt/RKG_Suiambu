package com.diagnostic.bluetoothtool.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.diagnostic.bluetoothtool.model.AudioPresetType
import com.diagnostic.bluetoothtool.model.ClassroomAudioProfile
import com.diagnostic.bluetoothtool.model.ClassroomAudioState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class ClassroomAudioPlayerManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val _audioState = MutableStateFlow(ClassroomAudioState())
    val audioState: StateFlow<ClassroomAudioState> = _audioState.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsInitialized = true
                    engine.setSpeechRate(_audioState.value.speechRate)
                    engine.setPitch(_audioState.value.pitch)
                    
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    engine.setAudioAttributes(audioAttributes)

                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _audioState.update { it.copy(isPlaying = true) }
                        }

                        override fun onDone(utteranceId: String?) {
                            _audioState.update { it.copy(isPlaying = false, activeText = "") }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _audioState.update { it.copy(isPlaying = false) }
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            _audioState.update { it.copy(isPlaying = false) }
                        }
                    })
                }
            }
        }
    }

    fun playTextInClassroom(text: String) {
        if (text.isBlank()) return
        
        // Ensure Bluetooth audio routing is prioritized
        routeToBluetoothAudio()

        _audioState.update { it.copy(activeText = text, isPlaying = true) }

        val params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, _audioState.value.activeProfile.volumeGain)
        
        tts?.setSpeechRate(_audioState.value.speechRate)
        tts?.setPitch(_audioState.value.pitch)
        
        val utteranceId = "classroom_speech_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopAudio() {
        tts?.stop()
        _audioState.update { it.copy(isPlaying = false, activeText = "") }
    }

    fun setSpeechRate(rate: Float) {
        val clamped = rate.coerceIn(0.5f, 2.5f)
        _audioState.update { it.copy(speechRate = clamped) }
        tts?.setSpeechRate(clamped)
    }

    fun setPitch(pitch: Float) {
        val clamped = pitch.coerceIn(0.5f, 2.0f)
        _audioState.update { it.copy(pitch = clamped) }
        tts?.setPitch(clamped)
    }

    fun applyAudioProfile(preset: AudioPresetType) {
        val profile = when (preset) {
            AudioPresetType.CLASSROOM_VOCAL -> ClassroomAudioProfile(
                type = preset,
                title = "Classroom Vocal Clarity",
                description = "Balanced human voice EQ tuned for classroom lecture retention",
                speechRate = 1.15f,
                pitch = 1.0f,
                volumeGain = 1.0f,
                isWhisperMode = false
            )
            AudioPresetType.STEALTH_WHISPER -> ClassroomAudioProfile(
                type = preset,
                title = "Stealth Whisper Mode",
                description = "Quiet, high-frequency audio routed specifically to smart glass earpiece",
                speechRate = 1.0f,
                pitch = 0.9f,
                volumeGain = 0.45f,
                isWhisperMode = true
            )
            AudioPresetType.LECTURE_SPEED_2X -> ClassroomAudioProfile(
                type = preset,
                title = "Fast Lecture Review (1.75x)",
                description = "Rapid playback mode for quick study reviews and key formulas",
                speechRate = 1.75f,
                pitch = 1.05f,
                volumeGain = 1.0f,
                isWhisperMode = false
            )
            AudioPresetType.HIGH_CLARITY_BOOST -> ClassroomAudioProfile(
                type = preset,
                title = "High Clarity Boost (+6dB)",
                description = "Maximum clarity & gain for noisy environments or far classroom seating",
                speechRate = 1.0f,
                pitch = 1.1f,
                volumeGain = 1.2f,
                isWhisperMode = false
            )
        }

        _audioState.update {
            it.copy(
                activeProfile = profile,
                speechRate = profile.speechRate,
                pitch = profile.pitch
            )
        }
        tts?.setSpeechRate(profile.speechRate)
        tts?.setPitch(profile.pitch)
    }

    private fun routeToBluetoothAudio() {
        try {
            audioManager?.let { am ->
                if (am.isBluetoothA2dpOn || am.isBluetoothScoAvailableOffCall) {
                    am.isSpeakerphoneOn = false
                    am.mode = AudioManager.MODE_NORMAL
                }
            }
        } catch (_: Exception) {}
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
