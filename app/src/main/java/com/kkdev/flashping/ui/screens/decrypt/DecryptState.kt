package com.kkdev.flashping.ui.screens.decrypt

import com.kkdev.flashping.ui.screens.encrpyt.EncryptionMethod

data class DecryptState(
    val message: String = "",
    val selectedMethod: EncryptionMethod = EncryptionMethod.MORSE,
    val decryptedMessage: String? = null,
    val isDecrypting: Boolean = false
)
