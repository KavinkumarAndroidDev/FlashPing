package com.kkdev.flashping.ui.screens.decrypt

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkdev.flashping.ui.screens.FunkyButton
import com.kkdev.flashping.ui.screens.encrpyt.EncryptionMethodSelector
import com.kkdev.flashping.ui.theme.*

@Composable
fun DecryptScreen(
    viewModel: DecryptViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToFlashlightInput: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = uiState.message,
            onValueChange = viewModel::onMessageChange,
            label = { Text("Enter code to decrypt") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PremiumDarkGray,
                unfocusedContainerColor = PremiumDarkGray,
                focusedIndicatorColor = PremiumYellow,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = PremiumYellow,
            )
        )
        Spacer(Modifier.height(24.dp))
        EncryptionMethodSelector(
            selectedMethod = uiState.selectedMethod,
            onMethodSelect = viewModel::onMethodSelect
        )
        Spacer(Modifier.height(24.dp))
        AnimatedVisibility(visible = uiState.decryptedMessage != null) {
            DecryptedResult(uiState.decryptedMessage ?: "")
        }
        Spacer(Modifier.height(24.dp))

        FunkyButton(text = "Decrypt from Flash", onClick = onNavigateToFlashlightInput)
        Spacer(Modifier.height(16.dp))

        FunkyButton(
            text = if (uiState.isDecrypting) "Decrypting..." else "Decrypt Text",
            onClick = viewModel::onConfirmDecrypt,
            enabled = uiState.message.isNotBlank() && !uiState.isDecrypting
        )
    }
}

@Composable
fun DecryptedResult(result: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PremiumDarkGray, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Decrypted Message", color = PremiumYellow, fontWeight = FontWeight.Bold)
        SelectionContainer {
            Text(result, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}
