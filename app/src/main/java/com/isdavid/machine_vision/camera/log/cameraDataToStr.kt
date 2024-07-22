package com.isdavid.machine_vision.camera.log

import com.isdavid.log.formatStringsIntoColumns
import com.isdavid.machine_vision.camera.CameraData

fun cameraDataToStr(cameraData: CameraData): String {
    val stringBuilder = StringBuilder()
    stringBuilder.appendLine("Camera ID: ${cameraData.cameraId}")
    stringBuilder.appendLine("  Lens Orientation ID: ${cameraData.facingId}")
    stringBuilder.appendLine("  Lens Orientation: ${cameraData.facing}")

    stringBuilder.appendLine("  Focal Lengths: ${cameraData.focalLengths.joinToString(", ") { "${it}mm" }}")

    cameraData.formats.forEach { format ->
        stringBuilder.appendLine("\n  Format Name: ${format.name}")
        stringBuilder.appendLine("    Format ID: ${format.id}")
        stringBuilder.appendLine("    Resolutions ${format.resolutions.size}:")

        val resolutions = formatStringsIntoColumns(
            format.resolutions.map { "${it.width}x${it.height}" },
            10, 8
        )

        stringBuilder.appendLine(resolutions.trimEnd())
    }

    return stringBuilder.toString()
}