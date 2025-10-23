package com.kkdev.flashping.service

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log

class FlashlightService(context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraId: String? by lazy {
        try {
            cameraManager.cameraIdList[0]
        } catch (e: Exception) {
            Log.e("FlashlightService", "Could not get camera ID", e)
            null
        }
    }

    fun turnOn() {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, true)
            } catch (e: Exception) {
                Log.e("FlashlightService", "Could not turn on flashlight", e)
            }
        }
    }

    fun turnOff() {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, false)
            } catch (e: Exception) {
                Log.e("FlashlightService", "Could not turn off flashlight", e)
            }
        }
    }
}
