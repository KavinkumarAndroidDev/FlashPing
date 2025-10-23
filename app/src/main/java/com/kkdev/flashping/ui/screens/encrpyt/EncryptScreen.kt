package com.kkdev.flashping.ui.screens.encrpyt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkdev.flashping.ui.screens.FunkyButton
import com.kkdev.flashping.ui.theme.PremiumBlack
import com.kkdev.flashping.ui.theme.PremiumDarkGray
import com.kkdev.flashping.ui.theme.PremiumYellow

@Composable
fun EncryptScreen(
    viewModel: EncryptViewModel,
    onNavigateToTransmission: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Allow scrolling for smaller screens
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = uiState.encryptedMessage == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Encrypt Message",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumYellow
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Enter the text you want to transmit. The message will be converted into a light signal.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(40.dp))

                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = viewModel::onMessageChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp),
                    label = { Text("Your message here") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PremiumYellow,
                        unfocusedBorderColor = PremiumDarkGray,
                        focusedLabelColor = PremiumYellow,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = PremiumYellow,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PremiumDarkGray,
                        unfocusedContainerColor = PremiumDarkGray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            if (uiState.message.isNotBlank()) {
                                viewModel.onConfirmEncrypt()
                            }
                        }
                    )
                )
                Spacer(Modifier.height(24.dp))

                EncryptionMethodSelector(
                    selectedMethod = uiState.selectedMethod,
                    onMethodSelect = viewModel::onMethodSelect
                )
                Spacer(Modifier.height(40.dp))

                FunkyButton(
                    text = if (uiState.isEncrypting) "Encrypting..." else "Confirm & Encrypt",
                    onClick = {
                        keyboardController?.hide()
                        viewModel.onConfirmEncrypt()
                    },
                    enabled = uiState.message.isNotBlank() && !uiState.isEncrypting
                )
            }
        }

        AnimatedVisibility(
            visible = uiState.encryptedMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            uiState.encryptedMessage?.let { encrypted ->
                EncryptedResultBox(
                    encryptedMessage = encrypted,
                    onProceed = onNavigateToTransmission,
                    onTryAgain = { viewModel.onTryAgain() }
                )
            }
        }
    }
}

@Composable
fun EncryptedResultBox(
    encryptedMessage: String,
    onProceed: () -> Unit,
    onTryAgain: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PremiumDarkGray)
            .border(1.dp, PremiumYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Encryption Successful", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PremiumYellow)
        Spacer(Modifier.height(16.dp))

        Text(
            text = encryptedMessage,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(PremiumBlack.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        )
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { clipboardManager.setText(AnnotatedString(encryptedMessage)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PremiumDarkGray),
                border = BorderStroke(1.dp, PremiumYellow)
            ) {
                Icon(Icons.Default.Send, "Copy", tint = PremiumYellow)
                Spacer(Modifier.width(8.dp))
                Text("Copy", color = PremiumYellow)
            }
            Button(
                onClick = onProceed,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PremiumYellow)
            ) {
                Text("Transmit", color = PremiumBlack, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, "Transmit", tint = PremiumBlack)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "or create a new message",
            color = Color.Gray,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onTryAgain() }
                .padding(8.dp)
        )
    }
}

@Composable
fun EncryptionMethodSelector(
    selectedMethod: EncryptionMethod,
    onMethodSelect: (EncryptionMethod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PremiumDarkGray)
            .border(1.dp, PremiumYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EncryptionMethod.values().forEach { method ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedMethod == method) PremiumYellow else Color.Transparent)
                    .clickable(
                        enabled = selectedMethod != method,
                        onClick = { onMethodSelect(method) }
                    )
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = method.name.replace('_', ' '),
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMethod == method) PremiumBlack else Color.White
                )
            }
        }
    }
}
