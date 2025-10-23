package com.kkdev.flashping.ui.screens.encrpyt

data class EncryptState(
    val message: String = "",
    val selectedMethod: EncryptionMethod = EncryptionMethod.MORSE,
    val encryptedMessage: String? = null,
    val isEncrypting: Boolean = false,
    val isTransmitting: Boolean = false,
    val transmissionProgress: Float = 0f,
    val transmissionSpeed: TransmissionSpeed = TransmissionSpeed.Normal
)

enum class EncryptionMethod(val displayName: String) {
    MORSE("Morse Code"),
    SHADOW_CIPHER("Shadow Cipher")
}

enum class TransmissionSpeed(val timeUnit: Long) {
    Slow(400L),
    Normal(250L),
    Fast(150L),
    Rapid(80L)
}
