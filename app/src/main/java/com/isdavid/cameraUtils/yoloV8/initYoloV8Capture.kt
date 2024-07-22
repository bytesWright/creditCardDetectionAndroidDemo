package com.isdavid.cameraUtils.yoloV8

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.isdavid.cameraUtils.cameraData.CameraData
import com.isdavid.cameraUtils.cameraData.queryDefaultCameraData
import com.isdavid.cameraUtils.cameraData.queryMaxResolution
import com.isdavid.cameraUtils.openCamera
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.P)
suspend fun initYoloV8Capture(context: Context, textureView: TextureView, generalCameraTaskHandler: Handler, onDetection: YoloV8onDetect): YoloV8CaptureWrapper {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    val cameraData = CameraData.queryDefaultCameraData(context)
    val maxResolution = cameraData.queryMaxResolution(ImageFormat.JPEG)

    val (previewSurface, camera) = runBlocking {
        val surface = textureView.retrieveSurface(maxResolution)
        val camera: CameraDevice = openCamera(cameraManager, cameraData.cameraId, generalCameraTaskHandler)

        Pair(surface, camera)
    }

    val yoloV8CaptureWrapper = YoloV8CaptureWrapper(
        context,
        camera,
        previewSurface,
        onDetection,
        maxResolution,
        generalCameraTaskHandler
    )

    return yoloV8CaptureWrapper
}





