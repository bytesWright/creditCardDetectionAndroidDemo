package com.isdavid.cameraUtils.cameraData

fun CameraData.queryMaxResolution(imageFormat: Int): Resolution =
    formatsById.getOrDefault(imageFormat, null)?.resolutions?.get(0)
        ?: throw RuntimeException("Could not query max resolution for format ${computeFormatName(imageFormat)}")
