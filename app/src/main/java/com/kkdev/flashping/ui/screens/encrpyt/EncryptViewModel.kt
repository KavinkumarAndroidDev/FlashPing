package com.kkdev.flashping.ui.screens.encrpyt

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkdev.flashping.service.FlashlightService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.text.iterator

private object MorseCode {
    private val codeMap: Map<Char, String> = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.", 'G' to "--.", 'H' to "....",
        'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.",
        'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.", '.' to ".-.-.-", ',' to "--..--",
        '?' to "..--..", '\'' to ".----.", '!' to "-.-.--", '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-", '&' to ".-...",
        ':' to "---...", ';' to "-.-.-.", '=' to "-...-", '+' to ".-.-.", '-' to "-....-", '_' to "..--.-", '"' to ".-..-.",
        '$' to "...-..-", '@' to ".--.-.", 'Ä' to ".-.-", 'À' to ".--.-", 'Å' to ".--.-", 'Ą' to ".-.-", 'Æ' to ".-.-",
        'Ć' to "-.-..", 'Ĉ' to "-.-..", 'Ç' to "-.-..", 'É' to "..-..", 'È' to ".-..-", 'Ę' to "..-..", 'Ö' to "---.",
        'Ó' to "---.", 'Ø' to "---.", 'Ü' to "..--", 'Ŭ' to "..--"
    )

    fun encrypt(char: Char): String {
        return codeMap[char.uppercaseChar()] ?: "?"
    }
}


class EncryptViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EncryptState())
    val uiState = _uiState.asStateFlow()

    private var flashlightService: FlashlightService? = null

    fun onSpeedSelected(speed: TransmissionSpeed) {
        _uiState.update { it.copy(transmissionSpeed = speed) }
    }
    private fun morseEncrypt(input: String): String {
        if (input.isBlank()) return ""
        val morseWords = input.trim().split(" ").map { word ->
            word.map { char -> MorseCode.encrypt(char) }.joinToString(separator = " ")
        }
        return morseWords.joinToString(separator = " / ")
    }

    private fun shadowCipherEncrypt(input: String): String {
        val standardMorse = morseEncrypt(input)
        return standardMorse.map {
            when (it) {
                '.' -> '-'
                '-' -> '.'
                else -> it
            }
        }.joinToString("").reversed()
    }

    private fun calculateTotalTime(morseString: String, timeUnit: Long): Long {
        if (timeUnit == 0L) return 1L
        var totalUnits = 0L
        morseString.forEachIndexed { index, char ->
            totalUnits += when (char) {
                '.' -> 1
                '-' -> 3
                else -> 0
            }
            if (char == '.' || char == '-') {
                val nextChar = morseString.getOrNull(index + 1)
                totalUnits += when (nextChar) {
                    '/' -> 7
                    ' ' -> 3
                    null -> 0
                    else -> 1
                }
            }
        }
        return totalUnits * timeUnit
    }


    fun onMessageChange(newMessage: String) {
        _uiState.update {
            it.copy(
                message = newMessage,
                encryptedMessage = null,
                isTransmitting = false,
                transmissionProgress = 0f
            )
        }
    }

    fun onMethodSelect(method: EncryptionMethod) {
        _uiState.update {
            it.copy(
                selectedMethod = method,
                encryptedMessage = null,
                isTransmitting = false,
                transmissionProgress = 0f
            )
        }
    }

    fun onConfirmEncrypt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isEncrypting = true, encryptedMessage = null) }
            delay(500)
            val originalMessage = _uiState.value.message
            val encryptedResult = when (_uiState.value.selectedMethod) {
                EncryptionMethod.MORSE -> this@EncryptViewModel.morseEncrypt(originalMessage)
                EncryptionMethod.SHADOW_CIPHER -> this@EncryptViewModel.shadowCipherEncrypt(originalMessage)
            }
            _uiState.update { it.copy(isEncrypting = false, encryptedMessage = encryptedResult) }
        }
    }

    fun onTryAgain() {
        _uiState.update {
            it.copy(
                encryptedMessage = null,
                transmissionProgress = 0f,
                isTransmitting = false
            )
        }
    }

    fun startTransmission(context: Context) {
        if (_uiState.value.isTransmitting) return
        val morseString = uiState.value.encryptedMessage ?: return
        val timeUnit = uiState.value.transmissionSpeed.timeUnit // Use selected speed

        if (flashlightService == null) {
            flashlightService = FlashlightService(context)
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTransmitting = true, transmissionProgress = 0f) }

            val totalDuration = calculateTotalTime(morseString, timeUnit)
            var elapsedTime = 0L

            for ((index, char) in morseString.withIndex()) {
                if (!_uiState.value.isTransmitting) break

                if (char == '.' || char == '-') {
                    flashlightService?.turnOn()
                }

                val pulseDuration = when (char) {
                    '.' -> timeUnit * 1 // Dot duration
                    '-' -> timeUnit * 3 // Dash duration
                    else -> 0L
                }
                if(pulseDuration > 0) delay(pulseDuration)

                flashlightService?.turnOff()

                val nextChar = morseString.getOrNull(index + 1)
                val gapDuration = when (nextChar) {
                    '/' -> timeUnit * 7
                    ' ' -> timeUnit * 3
                    null -> 0L
                    else -> timeUnit * 1
                }

                if (gapDuration > 0) delay(gapDuration)

                elapsedTime += pulseDuration + gapDuration
                val progress = if (totalDuration > 0) {
                    (elapsedTime.toFloat() / totalDuration).coerceIn(0f, 1f)
                } else 0f
                _uiState.update { it.copy(transmissionProgress = progress) }
            }
            _uiState.update { it.copy(isTransmitting = false, transmissionProgress = if (_uiState.value.isTransmitting) 0f else 1f) }
        }
    }

    fun stopTransmission() {
        flashlightService?.turnOff()
        _uiState.update { it.copy(isTransmitting = false) }
    }
}
