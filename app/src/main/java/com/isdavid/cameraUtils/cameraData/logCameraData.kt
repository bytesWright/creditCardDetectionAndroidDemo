package com.isdavid.cameraUtils.cameraData

fun logCameraData(cameraData: CameraData) {
    println("Camera ID: ${cameraData.cameraId}")
    println("Lens Orientation ID: ${cameraData.lensOrientationId}")
    println("Lens Orientation: ${cameraData.lensOrientation}")

    println("Formats:")
    cameraData.formats.forEach { format ->
        println("  Format ID: ${format.id}")
        println("  Format Name: ${format.name}")
        println("  Resolutions: ${format.resolutions.joinToString(", ") { "${it.width}x${it.height}" }}")
    }

    println("Formats By ID:")
    cameraData.formatsById.forEach { (id, format) ->
        println("  Format ID: $id")
        println("  Format Name: ${format.name}")
        println("  Resolutions: ${format.resolutions.joinToString(", ") { "${it.width}x${it.height}" }}")
    }

    println("Focal Lengths: ${cameraData.focalLengths.joinToString(", ") { "${it}mm" }}")
}