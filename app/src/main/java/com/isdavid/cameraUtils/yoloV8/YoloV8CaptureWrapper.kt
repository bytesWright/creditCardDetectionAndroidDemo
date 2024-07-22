package com.isdavid.cameraUtils.yoloV8

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.view.Surface
import androidx.annotation.RequiresApi
import com.isdavid.cameraUtils.BitmapConversion
import com.isdavid.cameraUtils.ImageConversion
import com.isdavid.cameraUtils.cameraData.Resolution
import com.isdavid.cameraUtils.createCaptureSession
import com.isdavid.cameraUtils.yoloV8.boundingBox.BoundingBox
import com.isdavid.handlers.LatestMessageProcessor
import kotlinx.coroutines.runBlocking


typealias IntermediateBitmapData = Pair<ByteArray, Pair<Int, Int>>
typealias DetectionResult = Pair<Bitmap, List<BoundingBox>>

@RequiresApi(Build.VERSION_CODES.P)
class YoloV8CaptureWrapper(
    context: Context,
    private val camera: CameraDevice,
    private val previewSurface: Surface,
    onDetection: YoloV8onDetect,
    captureResolution: Resolution,
    private val handler: Handler,
) {

    private val session: CameraCaptureSession
    private val yoloV8ModelWrapper = YoloV8ModelWrapper(
        context,
        tryToGpuAccelerate = true,
        onDetection = onDetection
    )

    private val analysisImageReader: ImageReader = ImageReader.newInstance(
        captureResolution.width,
        captureResolution.height,
        ImageFormat.YUV_420_888,
        4
    )

    private val analysisSurface: Surface = analysisImageReader.surface

    private val analysisFlowHandler = LatestMessageProcessor<IntermediateBitmapData, DetectionResult>("analysisHandler") { (data, size) ->
        val (width, height) = size
        val jpgBytes = ImageConversion.nv21toJpegBytes(data, width, height)
        val bitmap = BitmapFactory.decodeByteArray(jpgBytes, 0, jpgBytes.size)

        val rotatedBitmap = BitmapConversion.rotate(bitmap, 90F)
        bitmap.recycle()

        val result = yoloV8ModelWrapper.detect(rotatedBitmap)
        Pair(rotatedBitmap, result)
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
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

            if (analysisFlowHandler.working) {
                image.close()
                return@setOnImageAvailableListener
            }

            val workerInput = Pair(
                ImageConversion.yuv420888imageToNv21bytes(image),
                Pair(image.width, image.height)
            )

            image.close()

            analysisFlowHandler.postWork(workerInput) {
                val (bitmap, _) = it
                bitmap.recycle()
            }

            /**
            val bitmap = ImageConversion.imageToBitmap(image)
            val rotatedBitmap = BitmapConversion.rotate(bitmap, 90F)

            bitmap.recycle()
            yoloV8ModelWrapper.detect(rotatedBitmap)
            rotatedBitmap.recycle()
            image.close()
             **/
        }, handler)
    }

    private fun assignPreviewRequest(generalCameraTaskHandler: Handler) {
        val captureRequestBuilder: CaptureRequest.Builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)

        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureResult.CONTROL_AF_MODE_CONTINUOUS_VIDEO
        )

        captureRequestBuilder.addTarget(previewSurface)
        captureRequestBuilder.addTarget(analysisSurface)

        session.setRepeatingRequest(
            captureRequestBuilder.build(),
            null,
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