package com.isdavid.machine_vision.camera.query

import android.hardware.camera2.CameraCharacteristics

fun computeLensFacingName(lensFacing: Int?): String {
    return when (lensFacing) {
        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
        CameraCharacteristics.LENS_FACING_BACK -> "Back"
        CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
        else -> "Unknown"
    }
}