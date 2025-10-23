package com.kkdev.flashping.ui.screens.decrypt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkdev.flashping.ui.screens.encrpyt.EncryptionMethod
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DecryptViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DecryptState())
    val uiState = _uiState.asStateFlow()

    private val morseToChar: Map<String, Char> by lazy { createMorseToCharMap() }

    fun onMessageChange(newMessage: String) {
        _uiState.update { it.copy(message = newMessage, decryptedMessage = null) }
    }

    fun onMethodSelect(method: EncryptionMethod) {
        _uiState.update { it.copy(selectedMethod = method, decryptedMessage = null) }
    }

    fun onConfirmDecrypt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDecrypting = true, decryptedMessage = null) }
            delay(500)
            val originalMessage = _uiState.value.message
            val decryptedResult = when (_uiState.value.selectedMethod) {
                EncryptionMethod.MORSE -> morseDecrypt(originalMessage)
                EncryptionMethod.SHADOW_CIPHER -> shadowCipherDecrypt(originalMessage)
            }
            _uiState.update { it.copy(isDecrypting = false, decryptedMessage = decryptedResult) }
        }
    }

    private fun morseDecrypt(input: String): String {
        return input.trim().split(" / ").joinToString(separator = " ") { word -> // <-- FIX: Add a space separator here
            word.split(" ").mapNotNull { code ->
                morseToChar[code]
            }.joinToString(separator = "")
        }
    }

    private fun shadowCipherDecrypt(input: String): String {
        val unreversed = input.reversed()
        val unscrambled = unreversed.map {
            when (it) {
                '-' -> '.'
                '.' -> '-'
                else -> it
            }
        }.joinToString("")
        return morseDecrypt(unscrambled)
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

