package com.kkdev.flashping.ui.screens.flashlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections

data class FlashlightState(
    val detectedMorse: String = "",
    val morseDecodedText: String = "",
    val shadowDecodedText: String = "",
    val averageLuminance: Double = 0.0,
    val timeUnit: Long = 100,
    val isLightOn: Boolean = false,
    val onThreshold: Double = 0.0,
    val offThreshold: Double = 0.0
)

class FlashlightViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FlashlightState())
    val uiState = _uiState.asStateFlow()

    private val luminanceHistory = Collections.synchronizedList(mutableListOf<Double>())
    private val movingAverageWindow = 4
    private var isCurrentlyLit = false

    private val ambientLuminanceHistory = Collections.synchronizedList(mutableListOf<Double>())
    private val ambientWindow = 50
    private var onThreshold = 200.0
    private var offThreshold = 180.0

    private var lastStateChangeTime = System.currentTimeMillis()
    private val pulseDurations = Collections.synchronizedList(mutableListOf<Long>())
    private val maxPulseSamples = 20
    private var timeUnit: Long = 100

    private val morseToChar: Map<String, Char> by lazy { createMorseToCharMap() }

    fun onLuminanceReceived(luminance: Double) {
        viewModelScope.launch {
            luminanceHistory.add(luminance)
            if (luminanceHistory.size > movingAverageWindow) luminanceHistory.removeAt(0)
            val smoothedLuminance = luminanceHistory.average()

            if (!isCurrentlyLit) {
                ambientLuminanceHistory.add(smoothedLuminance)
                if (ambientLuminanceHistory.size > ambientWindow) ambientLuminanceHistory.removeAt(0)
            }
            val baseAmbient = ambientLuminanceHistory.average().coerceAtLeast(10.0)
            onThreshold = baseAmbient + 60.0
            offThreshold = baseAmbient + 40.0

            val wasLit = isCurrentlyLit
            isCurrentlyLit = if (wasLit) {
                smoothedLuminance >= offThreshold
            } else {
                smoothedLuminance > onThreshold
            }

            if (wasLit != isCurrentlyLit) {
                val currentTime = System.currentTimeMillis()
                val duration = currentTime - lastStateChangeTime
                if (!isCurrentlyLit) { processPulse(duration) }
                else { processGap(duration) }
                lastStateChangeTime = currentTime
            }

            _uiState.update { it.copy(
                averageLuminance = smoothedLuminance,
                isLightOn = isCurrentlyLit,
                onThreshold = onThreshold,
                offThreshold = offThreshold
            ) }
        }
    }
    private fun processPulse(duration: Long) {
        pulseDurations.add(duration)
        if (pulseDurations.size > maxPulseSamples) pulseDurations.removeAt(0)
        calibrateTimeUnit()
        val dotDashThreshold = timeUnit * 2
        val morseChar = if (duration < dotDashThreshold) "." else "-"
        _uiState.update {
            val newMorse = it.detectedMorse + morseChar
            it.copy(
                detectedMorse = newMorse,
                morseDecodedText = morseDecrypt(newMorse),
                shadowDecodedText = shadowCipherDecrypt(newMorse)
            )
        }
    }

    private fun processGap(duration: Long) {
        val characterGapThreshold = timeUnit * 2
        val wordGapThreshold = timeUnit * 5
        val gapChar = when {
            duration > wordGapThreshold -> " / "
            duration > characterGapThreshold -> " "
            else -> ""
        }
        if (gapChar.isNotEmpty()) {
            _uiState.update { it.copy(detectedMorse = it.detectedMorse + gapChar) }
        }
    }

    private fun calibrateTimeUnit() {
        if (pulseDurations.isEmpty()) return
        val sortedPulses = pulseDurations.sorted()
        val sampleSize = (sortedPulses.size * 0.4).toInt().coerceAtLeast(1)
        val shortestPulses = sortedPulses.take(sampleSize)
        val newTimeUnit = shortestPulses.average().toLong().coerceIn(50, 400)
        timeUnit = newTimeUnit
        _uiState.update { it.copy(timeUnit = timeUnit) }
    }

    fun clear() {
        _uiState.value = FlashlightState()
        luminanceHistory.clear()
        ambientLuminanceHistory.clear()
        pulseDurations.clear()
        timeUnit = 100
        isCurrentlyLit = false
    }

    private fun morseDecrypt(input: String): String {
        return input.trim().split(" / ").joinToString(separator = " ") { word ->
            word.split(" ").mapNotNull { code -> morseToChar[code] }.joinToString(separator = "")
        }
    }

    private fun shadowCipherDecrypt(input: String): String {
        val standardMorse = input.map {
            when (it) {
                '.' -> '-'
                '-' -> '.'
                else -> it
            }
        }.joinToString("")
        return morseDecrypt(standardMorse)
    }

    private fun createMorseToCharMap(): Map<String, Char> {
        val map = mutableMapOf<String, Char>()
        map[".-"] = 'A'; map["-..."] = 'B'; map["-.-."] = 'C'; map["-.."] = 'D'; map["."] = 'E';
        map["..-."] = 'F'; map["--."] = 'G'; map["...."] = 'H'; map[".."] = 'I'; map[".---"] = 'J';
        map["-.-"] = 'K'; map[".-.."] = 'L'; map["--"] = 'M'; map["-."] = 'N'; map["---"] = 'O';
        map[".--."] = 'P'; map["--.-"] = 'Q'; map[".-."] = 'R'; map["..."] = 'S'; map["-"] = 'T';
        map["..-"] = 'U'; map["...-"] = 'V'; map[".--"] = 'W'; map["-..-"] = 'X'; map["-.--"] = 'Y';
        map["--.."] = 'Z'; map["-----"] = '0'; map[".----"] = '1'; map["..---"] = '2';
        map["...--"] = '3'; map["....-"] = '4'; map["....."] = '5'; map["-...."] = '6';
        map["--..."] = '7'; map["---.."] = '8'; map["----."] = '9';
        map[".-.-.-"] = '.'; map["--..--"] = ','; map["..--.."] = '?';
        return map
    }
}
