package com.isdavid.camera.histogram

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log

object CameraHelper {

    /**
     * Logs supported output sizes for a given image format and camera facing.
     * @param context The application context.
     * @param imageFormat The image format to query (e.g., ImageFormat.YUV_420_888).
     * @param cameraFacing The camera facing (e.g., CameraCharacteristics.LENS_FACING_BACK).
     */
    fun logSupportedOutputSizes(context: Context, imageFormat: Int, cameraFacing: Int = CameraCharacteristics.LENS_FACING_BACK, tag:String="CameraHelper") {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        cameraManager?.let {

            try {
                for (cameraId in it.cameraIdList) {
                    val characteristics = it.getCameraCharacteristics(cameraId)
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

                    if (facing != null && facing == cameraFacing) {
                        val map =
                            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        map?.let { confMap ->
                            val sizes = confMap.getOutputSizes(imageFormat)

                            if (sizes != null) {
                                for (size in sizes) {
                                    Log.d(
                                        tag,
                                        "Camera ID $cameraId: Supported Size - ${size.width}x${size.height}"
                                    )
                                }
                            } else {
                                Log.d(
                                    tag,
                                    "No sizes available for the specified format."
                                )
                            }
                        }
                        break // Remove this line if you want to log sizes for all cameras that match the specified facing.
                    }

                }
            } catch (e: CameraAccessException) {
                Log.e(tag, "Camera access exception", e)
            }
        } ?: run {
            Log.e(tag, "Failed to get CameraManager")
        }
    }
}
