package com.kkdev.flashping.ui.screens.flashlight

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

class FlashlightAnalyzer(
    private val onLuminance: (Double) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val plane = imageProxy.planes[0]
        val buffer: ByteBuffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)


        val totalLuminance = bytes.fold(0L) { acc, byte ->
            acc + (byte.toInt() and 0xFF)
        }

        val averageLuminance = totalLuminance.toDouble() / bytes.size.coerceAtLeast(1)

        onLuminance(averageLuminance)

        imageProxy.close()
    }
}
