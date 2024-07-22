package com.isdavid.machine_vision.yolo

import android.content.Context
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.view.Surface
import com.isdavid.log.Logger
import com.isdavid.machine_vision.camera.CameraData
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.camera.openCamera
import com.isdavid.machine_vision.camera.query.queryDefaultCameraData
import com.isdavid.machine_vision.camera.query.queryMaxSharedResolution
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import kotlinx.coroutines.runBlocking

val log = Logger.provide("YLV")

suspend fun buildYoloCaptureWrapper(
    context: Context,
    yoloModelWrapper: TflModelWrapper,
    generalCameraTaskHandler: Handler,
    onFirstCapture: () -> Unit = {},
    buildSurface: (PlaneShape) -> Surface,
    setViewAspectRatio: (PlaneShape) -> Unit = { }
): YoloVideoCapture {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    val cameraData = CameraData.queryDefaultCameraData(context)
    val sharedMaxResolution =
        cameraData.queryMaxSharedResolution(ImageFormat.JPEG, ImageFormat.YUV_420_888)

    log.line { "Shared max resolution $sharedMaxResolution" }
    log.line { "Orientation ${cameraData.orientation}" }

    val (previewSurface, camera) = runBlocking {
        val camera: CameraDevice = openCamera(
            cameraManager,
            cameraData.cameraId,
            generalCameraTaskHandler
        )

        log.line { "Camera opened" }

        val orientation = context.resources.configuration.orientation

        val previewSurface = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setViewAspectRatio(sharedMaxResolution)
            buildSurface(sharedMaxResolution)
        } else {
            setViewAspectRatio(sharedMaxResolution)
            buildSurface(sharedMaxResolution)
        }


        log.line { "Getting surface" }

        Pair(previewSurface, camera)
    }

    val yoloVideoCapture = YoloVideoCapture(
        yoloModelWrapper,
        camera,
        previewSurface,
        sharedMaxResolution,
        generalCameraTaskHandler,
        onFirstCapture = onFirstCapture
    )

    return yoloVideoCapture
}





