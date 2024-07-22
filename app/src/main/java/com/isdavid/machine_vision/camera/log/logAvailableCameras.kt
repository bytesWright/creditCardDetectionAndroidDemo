package com.isdavid.machine_vision.camera.log

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import android.util.Size
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat


fun logAvailableCameras(context: Context) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraIds = cameraManager.cameraIdList

            for (cameraId in cameraIds) {
                val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)

                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

                val lensFacingString = when (lensFacing) {
                    CameraCharacteristics.LENS_FACING_FRONT -> "Front"
                    CameraCharacteristics.LENS_FACING_BACK -> "Back"
                    CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
                    else -> "Unknown"
                }

                Log.d("CameraInfo", "Camera ID: $cameraId, Facing: $lensFacingString")

                val availableCapabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)

                val resolutionMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val resolutionOptions: Array<Size> = resolutionMap?.getOutputSizes(SurfaceTexture::class.java) ?: emptyArray()

                availableCapabilities?.let { it ->
                    val capabilitiesString = it.joinToString { cap: Int ->
                        when (cap) {
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> "Backward Compatible"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> "Manual Sensor"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> "Manual Post Processing"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> "RAW"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT -> "Depth Output"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> "Logical Multi-Camera"
                            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME -> "Monochrome"
                            else -> {
                                cap.toString()
                            }
                        }
                    }
                    Log.d("CameraInfo", "    Capabilities: $capabilitiesString")
                    Log.d(
                        "CameraInfo",
                        "    Resolution options: ${resolutionOptions.joinToString { size -> size.toString() }}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CameraInfo", "Error accessing camera characteristics", e)
        }
    }, ContextCompat.getMainExecutor(context))
}




