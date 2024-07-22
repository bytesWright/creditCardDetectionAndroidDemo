package com.isdavid.cameraUtils.cameraData

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap


fun CameraData.Companion.queryCamerasData(context: Context): List<CameraData> {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIdList = cameraManager.cameraIdList

    return cameraIdList.map { id ->
        val characteristics = cameraManager.getCameraCharacteristics(id)
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        val streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.toList() ?: emptyList()

        val formats = queryImageFormats(streamConfigurationMap)

        CameraData(
            cameraId = id,
            lensOrientationId = facing,
            lensOrientation = computeLensFacingName(facing),
            formats = formats,
            formatsById = formats.associateBy { it.id },
            focalLengths = focalLengths
        )
    }
}

fun CameraData.Companion.queryDefaultCameraData(context: Context): CameraData {
    return CameraData.queryCamerasData(context).find {
        it.hasFormat(ImageFormat.JPEG) &&
            it.lensOrientationId == CameraCharacteristics.LENS_FACING_BACK
    } ?: throw RuntimeException("No camera found")
}

fun computeLensFacingName(lensFacing: Int?): String {
    return when (lensFacing) {
        CameraCharacteristics.LENS_FACING_FRONT -> "Front"
        CameraCharacteristics.LENS_FACING_BACK -> "Back"
        CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
        else -> "Unknown"
    }
}

fun queryImageFormats(streamConfigurationMap: StreamConfigurationMap?): List<FormatData> {
    val map = streamConfigurationMap ?: return emptyList()

    return map.outputFormats.map { format ->
        val resolutions = (map.getOutputSizes(format)?.toList() ?: emptyList()).map {
            Resolution(
                it.width,
                it.height,
                it.width.toDouble() * it.height.toDouble()
            )
        }

        resolutions.sortedBy { it.area }

        FormatData(
            id = format,
            name = computeFormatName(format),
            resolutions = resolutions
        )
    }
}

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