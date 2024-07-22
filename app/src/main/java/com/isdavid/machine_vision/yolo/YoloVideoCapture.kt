package com.isdavid.machine_vision.yolo

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.util.Log

import android.view.Surface
import com.isdavid.common.handlers.LatestMessageProcessor
import com.isdavid.machine_vision.BitmapOperations
import com.isdavid.machine_vision.ImageConversion
import com.isdavid.machine_vision.camera.PlaneShape
import com.isdavid.machine_vision.camera.createCaptureSession
import com.isdavid.machine_vision.yolo.bundles.CameraStatus
import com.isdavid.machine_vision.yolo.bundles.IntermediateBitmapData
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean


class YoloVideoCapture(
    tflModelWrapper: TflModelWrapper,
    private val camera: CameraDevice,
    private val previewSurface: Surface,
    capturePlaneShape: PlaneShape,
    private val handler: Handler,
    onFirstCapture: () -> Unit = {}
) {
    private val session: CameraCaptureSession
    private var firstCaptureDone = false

    private val analysisImageReader: ImageReader = ImageReader.newInstance(
        capturePlaneShape.width,
        capturePlaneShape.height,
        ImageFormat.YUV_420_888,
        4
    )

    private val analysisSurface: Surface = analysisImageReader.surface

    private val analysisFlowHandler =
        LatestMessageProcessor<IntermediateBitmapData>("analysisHandler") { (data, size, cameraStatus) ->
            //Finish creating the bitmap here to improve fps

            val (width, height) = size
            val jpgBytes = ImageConversion.nv21toJpegBytes(data, width, height)
            val bitmap = BitmapFactory.decodeByteArray(jpgBytes, 0, jpgBytes.size)

            val rotatedBitmap = BitmapOperations.rotate(bitmap, 90F)
            bitmap.recycle()

            tflModelWrapper.detect(
                rotatedBitmap,
                cameraStatus
            )
        }

    private var inFocus = AtomicBoolean(false)

    private val captureCallBack = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            val focusState: Int = result.get(CaptureResult.CONTROL_AF_STATE) ?: return

            if (!firstCaptureDone) {
                firstCaptureDone = true
                onFirstCapture()
            }

            inFocus.set(
                when (focusState) {
                    CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED,
                    CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED -> true

                    else -> false
                }
            )
        }
    }

    init {
        prepareAnalysisImageReader()

        val surfaces = listOf(
            previewSurface,
            analysisSurface
        )

        session = runBlocking {
            createCaptureSession(camera, surfaces)
        }

        assignPreviewRequest(handler)
    }

    private fun prepareAnalysisImageReader() {
        analysisImageReader.setOnImageAvailableListener({ reader ->

            val image: Image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            try {
                if (analysisFlowHandler.working) {
                    image.close()
                    return@setOnImageAvailableListener
                }

                // Not creating the bitmap in the capture thread helps with fluidity in the UI
                // That is why we only take an intermediary step

                val workerInput = IntermediateBitmapData(
                    ImageConversion.YUV_420_888toNV21(image),
                    arrayOf(image.width, image.height),
                    CameraStatus(inFocus.get())
                )

                analysisFlowHandler.postWork(workerInput)
            } catch (ex: Exception) {
                Log.e("YoloVideoCapture", "Error while processing image", ex)
            } finally {
                image.close()
            }
        }, handler)
    }

    private fun assignPreviewRequest(generalCameraTaskHandler: Handler) {
        val captureRequestBuilder: CaptureRequest.Builder =
            camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)

        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureResult.CONTROL_AF_MODE_CONTINUOUS_VIDEO
        )

        captureRequestBuilder.addTarget(previewSurface)
        captureRequestBuilder.addTarget(analysisSurface)

        session.setRepeatingRequest(
            captureRequestBuilder.build(),
            captureCallBack,
            generalCameraTaskHandler
        )
    }

    fun close() {
        analysisImageReader.close()
        analysisSurface.release()
        analysisFlowHandler.quitSafely()
        session.close()
    }
}

