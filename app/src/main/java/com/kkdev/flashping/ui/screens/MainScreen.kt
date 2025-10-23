package com.kkdev.flashping.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.kkdev.flashping.ui.navigation.Screen
import com.kkdev.flashping.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MainScreen(navController: NavController) {
    var showInfoModal by remember { mutableStateOf(false) }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                InfoFloatingActionButton { showInfoModal = true }
            },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            MainContent(
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }

    if (showInfoModal) {
        HowItWorksModal(onDismiss = { showInfoModal = false })
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier, navController: NavController) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ScreenPopIn"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FlashPing",
            style = MaterialTheme.typography.headlineLarge.copy(
                shadow = Shadow(
                    color = PremiumYellow.copy(alpha = 0.3f),
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 100.dp)
        )

        FunkyButton(text = "Encrypt Message", onClick = {
            navController.navigate(Screen.Encrypt.route)
        })
        Spacer(modifier = Modifier.height(24.dp))
        FunkyButton(text = "Decrypt Message", onClick = { navController.navigate(Screen.Decrypt.route)
        })
    }
}

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PremiumDarkGray,
                        PremiumBlack
                    )
                )
            )
    ) {
        ConstellationEffect()
        content()
    }
}

@Composable
fun InfoFloatingActionButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "FabScale"
    )
    FloatingActionButton(
        onClick = onClick,
        interactionSource = interactionSource,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(16.dp),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp),
        modifier = Modifier
            .padding(bottom = 16.dp)
            .scale(scale)
    ) {
        Icon(imageVector = Icons.Default.Info, contentDescription = "How it works", modifier = Modifier.size(28.dp))
    }
}

@Composable
fun FunkyButton(text: String, onClick: () -> Unit, enabled: Boolean = true ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "ButtonScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        label = "ButtonAlpha"
    )
    Button(
        onClick = { if (enabled) onClick() },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 12.dp, bottomStart = 28.dp, bottomEnd = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary ,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
            .alpha(alpha),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp),
        enabled = enabled
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HowItWorksModal(onDismiss: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ModalScale"
    )
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .scale(scale) // Assuming you have a scale animation
                .background(PremiumDarkGray.copy(alpha = 0.95f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "How FlashPing Works",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // Updated detailed content
            Text(
                "1. Encrypt: Type any message and choose an encryption method.\n\n" +
                        "2. Transmit: Use your phone's flashlight to send the encrypted message as Morse code.\n\n" +
                        "3. Decrypt: Type in a Morse code message or use your camera to read a flashing light and decode it in real-time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start // Changed for better readability
            )
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Got It!", fontWeight = FontWeight.Bold)
            }
        }
    }
}



@Composable
fun ConstellationEffect() {
    val particles = remember { List(100) { Particle() } }
    val transition = rememberInfiniteTransition(label = "Constellation")
    val animationProgress by transition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart), label = "ParticleProgress")
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentOffset = particle.getOffset(animationProgress, size)
            drawCircle(color = PremiumWhite, radius = particle.radius, center = currentOffset, alpha = particle.alpha)
        }
    }
}

private data class Particle(
    val startXFactor: Float = Random.nextFloat(),
    val startYFactor: Float = Random.nextFloat(),
    val speedFactor: Float = Random.nextFloat() * 0.5f + 0.5f,
    val angle: Float = Random.nextFloat() * 360f,
    val radius: Float = Random.nextFloat() * 2f + 1f,
    val alpha: Float = Random.nextFloat() * 0.5f + 0.1f
) {
    fun getOffset(progress: Float, canvasSize: Size): Offset {
        val distance = (progress * speedFactor * 200f) % 200f
        val currentX = (startXFactor * canvasSize.width + cos(angle) * distance)
        val currentY = (startYFactor * canvasSize.height + sin(angle) * distance)
        return Offset(currentX, currentY)
    }
}
