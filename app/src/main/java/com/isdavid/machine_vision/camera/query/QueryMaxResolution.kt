package com.isdavid.machine_vision.camera.query

import com.isdavid.common.collections.getOrThrow
import com.isdavid.machine_vision.camera.CameraData
import com.isdavid.machine_vision.camera.PlaneShape

fun CameraData.queryMaxResolution(imageFormat: Int): PlaneShape =
    formatsById.getOrDefault(imageFormat, null)?.resolutions?.get(0)
        ?: throw RuntimeException(
            "Could not query max resolution for format ${
                computeFormatName(
                    imageFormat
                )
            }"
        )

fun CameraData.querySharedResolutions(imageFormatA: Int, imageFormatB: Int): List<PlaneShape> {
    val resolutionsA = formatsById.getOrThrow(imageFormatA, "Could not find format $imageFormatA")
    val resolutionsB = formatsById.getOrThrow(imageFormatB, "Could not find format $imageFormatB")

    return resolutionsA.resolutions
        .intersect(resolutionsB.resolutions.toSet())
        .toList()
        .sortedByDescending { it.area }
}

fun CameraData.queryMaxSharedResolution(imageFormatA: Int, imageFormatB: Int): PlaneShape =
    querySharedResolutions(imageFormatA, imageFormatB)
        .getOrThrow(0, "No shared resolutions")

