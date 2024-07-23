package com.isdavid.credit_card_detection.view_model.delegates

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import com.isdavid.common.coroutines.CoroutineHolderC
import com.isdavid.credit_card_detection.view_model.CardSide
import com.isdavid.detection_preview.view_model.YoloVMD
import com.isdavid.detection_preview.view_model.YoloVMDC
import com.isdavid.machine_vision.BitmapOperations
import com.isdavid.machine_vision.ocr.OcrWrapper
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBox
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBoxes
import com.isdavid.machine_vision.yolo.bundles.CameraStatus
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelRemoteControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration


class CaptureSideVMD(
    context: Context,
    private val yoloVMD: YoloVMD = YoloVMD(context)
) : YoloVMDC by yoloVMD, CoroutineHolderC by yoloVMD {

    var onSideDetected: ((detected: Bitmap, cardSide: CardSide, extractedText: ExtractedPhrases) -> Unit)? = null

    private var firstDetectionTime: Long = -1
    private var timeInFrame: Long = -1
    private val minimumTimeInFrame = 500
    private val ocrWrapper = OcrWrapper()

    init {
        yoloVMD.onDetect = this::onDetect
    }

    private fun onDetect(
        sourceImage: Bitmap,
        boundingBoxes: BoundingBoxes,
        cameraStatus: CameraStatus?,
        controlRemote: TflModelRemoteControl,
        executionTime: Duration
    ) {
        if (cameraStatus?.inFocus != true) return
        yoloVMD.updateBoxes(boundingBoxes)

        val creditCardSideBBox = boundingBoxes.find {
            it.classId == 0 || it.classId == 1
        }

        if (creditCardSideBBox == null) {
            sourceImage.recycle()

            firstDetectionTime = -1
            timeInFrame = -1
            return
        }

        val currentTime: Long = System.currentTimeMillis()

        if (firstDetectionTime < 0) {
            firstDetectionTime = currentTime
            return
        }

        timeInFrame = currentTime - firstDetectionTime
        if (timeInFrame < minimumTimeInFrame) return

        controlRemote.pause()

        localScope.launch(Dispatchers.Default) {
            parseImage(
                sourceImage,
                creditCardSideBBox,
                controlRemote
            )
        }
    }

    private fun parseImage(
        sourceImage: Bitmap,
        creditCardBBox: BoundingBox,
        controlRemote: TflModelRemoteControl,

        ) {
        val crop = BitmapOperations.crop(
            sourceImage,
            creditCardBBox
        )

        val cardSide = CardSide.build(creditCardBBox.classId)

        val text: Text = ocrWrapper.run(crop)

        val dataMaps = text.textBlocks.map {
            extractData(it.text)
        }

        val consolidatedValues = consolidateMaps(dataMaps)

        onSideDetected?.invoke(crop, cardSide, consolidatedValues)
        controlRemote.resume()
    }
}




