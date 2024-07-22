package com.isdavid.machine_vision.yolo.views

import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import com.isdavid.log.Logger
import com.isdavid.machine_vision.camera.PlaneShape
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun TextureView.retrieveSurface(planeShape: PlaneShape): Surface =
    suspendCancellableCoroutine { continuation ->
        fun buildSurface(surface: SurfaceTexture): Surface {
            surface.setDefaultBufferSize(planeShape.width, planeShape.height)
            val previewSurface = Surface(surfaceTexture)
            return previewSurface
        }

        if (isAvailable) {
            val surfaceTexture =
                surfaceTexture ?: throw RuntimeException("TextureView was not truly ready")
            val surface = buildSurface(surfaceTexture)
            continuation.resume(surface)
            return@suspendCancellableCoroutine
        }

        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                val surface = buildSurface(surfaceTexture)
                continuation.resume(surface)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }



suspend fun SurfaceView.retrieveSurface(): Surface =
    suspendCancellableCoroutine { continuation ->
        log.line { "> Retrieving surface <" }

        val surface: Surface? = holder.surface

        if (surface != null) {
            log.line { "> Surface created <" }
            continuation.resume(surface)
            return@suspendCancellableCoroutine
        }

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                log.line { "> Surface created <" }
                continuation.resume(holder.surface)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                log.line { "> Surface changed < $format $width $height" }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                log.line { "> Surface destroyed <" }
            }
        })
    }