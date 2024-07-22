package com.isdavid.cameraUtils

import android.annotation.SuppressLint
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@SuppressLint("MissingPermission")
suspend fun openCamera(cameraManager: CameraManager, cameraId: String, handler: Handler? = null): CameraDevice =
    suspendCancellableCoroutine { continuation ->

        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

            override fun onOpened(device: CameraDevice) = continuation.resume(device)

            override fun onDisconnected(device: CameraDevice) {
            }

            override fun onError(device: CameraDevice, error: Int) {
                val message = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }

                if (continuation.isActive) {
                    val runtimeException = RuntimeException("Camera $cameraId error: ($error) $message")
                    continuation.resumeWithException(runtimeException)
                }
            }
        }, handler)
    }