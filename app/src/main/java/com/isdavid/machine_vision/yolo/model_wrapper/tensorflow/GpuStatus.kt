package com.isdavid.machine_vision.yolo.model_wrapper.tensorflow

import android.content.Context
import android.content.pm.PackageManager

class GpuStatus(context: Context) {
    val available: Boolean

    init {
        val packageManager = context.packageManager
        val feature = PackageManager.FEATURE_OPENGLES_EXTENSION_PACK
        available = packageManager.hasSystemFeature(feature)
    }
}