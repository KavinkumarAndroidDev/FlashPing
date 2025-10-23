package com.kkdev.flashping.ui.screens.flashlight

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kkdev.flashping.ui.theme.PremiumBlack
import com.kkdev.flashping.ui.theme.PremiumDarkGray
import com.kkdev.flashping.ui.theme.PremiumYellow
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.foundation.layout.Arrangement
import com.kkdev.flashping.ui.screens.FunkyButton

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FlashlightInputScreen(
    viewModel: FlashlightViewModel = viewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val uiState by viewModel.uiState.collectAsState()

   Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(16.dp)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        if (cameraPermissionState.status.isGranted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Morse Decrypter", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PremiumYellow)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                ) {
                    CameraPreview(onLuminance = viewModel::onLuminanceReceived)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PremiumDarkGray, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(uiState.detectedMorse.ifBlank { "Aim at light source..." }, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        ResultRow("Morse:", uiState.morseDecodedText)
                        ResultRow("Shadow:", uiState.shadowDecodedText)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            DebugInfo("Luma/ON", "%.0f / %.0f".format(uiState.averageLuminance, uiState.onThreshold))
                            DebugInfo("Light On?", uiState.isLightOn.toString())
                            DebugInfo("Time Unit", "${uiState.timeUnit} ms")
                        }
                    }

                    FunkyButton(text = "Clear", onClick = { viewModel.clear() })
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission is required to decrypt signals.", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                FunkyButton(text = "Grant Permission", onClick = { cameraPermissionState.launchPermissionRequest() })
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = PremiumYellow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Text(text.ifBlank { "..." }, color = Color.White, fontSize = 18.sp)
    }
}

@Composable
private fun DebugInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), color = PremiumYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}


@Composable
fun CameraPreview(onLuminance: (Double) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, FlashlightAnalyzer(onLuminance))
                    }

                try {
                    cameraProvider.unbindAll()

                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )

                    camera.cameraControl.enableTorch(false)

                    val factory = SurfaceOrientedMeteringPointFactory(
                        previewView.width.toFloat(),
                        previewView.height.toFloat()
                    )
                    val point = factory.createPoint(previewView.width / 2f, previewView.height / 2f)
                    val action = FocusMeteringAction.Builder(point).build()
                    camera.cameraControl.startFocusAndMetering(action)

                    camera.cameraControl.setExposureCompensationIndex(0)


                } catch (e: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", e)
                }
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
