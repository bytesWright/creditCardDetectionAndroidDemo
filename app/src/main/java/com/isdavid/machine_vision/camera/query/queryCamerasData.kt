package com.isdavid.machine_vision.camera.query

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.isdavid.machine_vision.camera.CameraData


fun CameraData.Companion.queryCamerasData(context: Context): List<CameraData> {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIdList = cameraManager.cameraIdList

    return cameraIdList.map { id ->
        val characteristics = cameraManager.getCameraCharacteristics(id)

        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        val orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)

        val streamConfigurationMap =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

        val focalLengths =
            characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.toList()
                ?: emptyList()

        val formats = queryImageFormats(streamConfigurationMap)

        CameraData(
            cameraId = id,
            orientation = orientation,
            facingId = facing,
            facing = computeLensFacingName(facing),
            formats = formats,
            formatsById = formats.associateBy { it.id },
            focalLengths = focalLengths
        )
    }
}

