package com.isdavid.cameraUtils

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@RequiresApi(Build.VERSION_CODES.P)
suspend fun createCaptureSession(
    device: CameraDevice,
    targets: List<Surface>
): CameraCaptureSession = suspendCancellableCoroutine { continuation ->
    val outputConfigurations = targets.map { OutputConfiguration(it) }

    val sessionConfiguration = SessionConfiguration(
        SessionConfiguration.SESSION_REGULAR,
        outputConfigurations,
        Executors.newSingleThreadExecutor(),

        object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                continuation.resume(session)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                continuation.resumeWithException(RuntimeException("Could not create capture session"))
            }
        }
    )
    
    device.createCaptureSession(sessionConfiguration)
}