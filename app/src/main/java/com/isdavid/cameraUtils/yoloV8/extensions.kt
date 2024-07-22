package com.isdavid.cameraUtils.yoloV8

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import com.isdavid.cameraUtils.cameraData.Resolution
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun TextureView.retrieveSurface(resolution: Resolution): Surface = suspendCancellableCoroutine { continuation ->
    fun buildSurface(surface: SurfaceTexture): Surface {
        surface.setDefaultBufferSize(resolution.width, resolution.height)
        val previewSurface = Surface(surfaceTexture)
        return previewSurface
    }

    if (isAvailable) {
        val surfaceTexture = surfaceTexture ?: throw RuntimeException("TextureView was not truly ready")
        val surface = buildSurface(surfaceTexture)
        continuation.resume(surface)
        return@suspendCancellableCoroutine
    }


    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            val surface = buildSurface(surfaceTexture)
            continuation.resume(surface)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }
}