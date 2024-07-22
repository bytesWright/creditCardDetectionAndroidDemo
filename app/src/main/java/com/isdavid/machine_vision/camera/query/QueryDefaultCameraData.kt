package com.isdavid.machine_vision.camera.query

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import com.isdavid.machine_vision.camera.CameraData

fun CameraData.Companion.queryDefaultCameraData(context: Context): CameraData {
    return CameraData.queryCamerasData(context).find {
        it.hasFormat(ImageFormat.JPEG) &&
                it.facingId == CameraCharacteristics.LENS_FACING_BACK
    } ?: throw RuntimeException("No camera found")
}