package com.isdavid.machine_vision.camera.query

import android.graphics.ImageFormat

fun computeFormatName(format: Int): String {
    return when (format) {
        ImageFormat.JPEG -> "JPEG"
        ImageFormat.RAW_SENSOR -> "RAW_SENSOR"
        ImageFormat.RGB_565 -> "RGB_565"
        ImageFormat.YUV_420_888 -> "YUV_420_888"
        ImageFormat.YUV_422_888 -> "YUV_422_888"
        ImageFormat.YUV_444_888 -> "YUV_444_888"
        ImageFormat.FLEX_RGB_888 -> "FLEX_RGB_888"
        ImageFormat.FLEX_RGBA_8888 -> "FLEX_RGBA_8888"
        ImageFormat.PRIVATE -> "PRIVATE"
        ImageFormat.DEPTH16 -> "DEPTH16"
        ImageFormat.DEPTH_JPEG -> "DEPTH_JPEG"
        ImageFormat.DEPTH_POINT_CLOUD -> "DEPTH_POINT_CLOUD"
        ImageFormat.HEIC -> "HEIC"
        ImageFormat.JPEG_R -> "JPEG_R"
        ImageFormat.NV16 -> "NV16"
        ImageFormat.NV21 -> "NV21"
        ImageFormat.RAW10 -> "RAW10"
        ImageFormat.RAW12 -> "RAW12"
        ImageFormat.RAW_PRIVATE -> "RAW_PRIVATE"
        ImageFormat.Y8 -> "Y8"
        ImageFormat.YV12 -> "YV12"
        ImageFormat.YCBCR_P010 -> "YCBCR_P010"
        ImageFormat.UNKNOWN -> "UNKNOWN"
        ImageFormat.YUY2 -> "YUY2"
        else -> "UNKNOWN"
    }
}