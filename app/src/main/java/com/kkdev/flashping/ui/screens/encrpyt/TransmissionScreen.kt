package com.kkdev.flashping.ui.screens.encrpyt

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kkdev.flashping.ui.screens.FunkyButton
import com.kkdev.flashping.ui.theme.PremiumBlack
import com.kkdev.flashping.ui.theme.PremiumDarkGray
import com.kkdev.flashping.ui.theme.PremiumYellow


@Composable
fun TransmissionScreen(
    viewModel: EncryptViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.transmissionProgress,
        label = "TransmissionProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                uiState.isTransmitting -> "Transmitting..."
                uiState.transmissionProgress > 0.99f -> "Transmission Complete"
                else -> "Ready to Transmit"
            },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PremiumYellow
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = when {
                uiState.isTransmitting -> "Please keep the device steady."
                uiState.transmissionProgress > 0.99f -> "The message has been sent successfully."
                else -> "Select a speed and press 'Start' to begin."
            },
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))


        when {
            uiState.isTransmitting -> {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = PremiumYellow,
                    trackColor = PremiumDarkGray
                )
                Spacer(Modifier.height(40.dp))
                FunkyButton(text = "Stop Transmission", onClick = { viewModel.stopTransmission() })
            }
            uiState.transmissionProgress > 0.99f -> {
                Spacer(Modifier.height(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //FunkyButton(text = "Transmit Again", onClick = { viewModel.onTryAgain() })
                    FunkyButton(text = "Create New Message", onClick = onNavigateBack)
                }
            }
            else -> {
                SpeedSelector(
                    selectedSpeed = uiState.transmissionSpeed,
                    onSpeedSelect = viewModel::onSpeedSelected
                )
                Spacer(Modifier.height(40.dp))
                Button(
                    onClick = { viewModel.startTransmission(context) },
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumYellow),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("START", fontSize = 32.sp,style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PremiumBlack)
                }
            }
        }
    }
}

// In /ui/screens/encrypt/TransmissionScreen.kt (or a shared components file)

@Composable
fun SpeedSelector(
    selectedSpeed: TransmissionSpeed,
    onSpeedSelect: (TransmissionSpeed) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Transmission Speed", color = PremiumYellow, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TransmissionSpeed.values().forEach { speed ->
                val isSelected = speed == selectedSpeed
                Text(
                    text = speed.name,
                    color = if (isSelected) PremiumBlack else PremiumYellow,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PremiumYellow else PremiumDarkGray)
                        .clickable { onSpeedSelect(speed) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                //Spacer(Modifier.width(8.dp))

            }
        }
    }
}
