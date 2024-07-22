package com.isdavid.machine_vision.camera.query

import android.hardware.camera2.params.StreamConfigurationMap
import com.isdavid.machine_vision.camera.CameraData
import com.isdavid.machine_vision.camera.FormatData
import com.isdavid.machine_vision.camera.PlaneShape

fun CameraData.Companion.queryImageFormats(streamConfigurationMap: StreamConfigurationMap?): List<FormatData> {
    val map = streamConfigurationMap ?: return emptyList()

    return map.outputFormats.map { format ->
        val planeShapes = (map.getOutputSizes(format)?.toList() ?: emptyList()).map {
            PlaneShape(
                it.width,
                it.height
            )
        }

        planeShapes.sortedBy { it.area }

        FormatData(
            id = format,
            name = computeFormatName(format),
            resolutions = planeShapes
        )
    }
}