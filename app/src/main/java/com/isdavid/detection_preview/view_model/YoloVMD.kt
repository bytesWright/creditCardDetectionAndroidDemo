package com.isdavid.detection_preview.view_model

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.isdavid.common.coroutines.CoroutineHolder
import com.isdavid.common.coroutines.CoroutineHolderC
import com.isdavid.machine_vision.BitmapOperations
import com.isdavid.machine_vision.BitmapOperations.Companion.createTransparent
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBox
import com.isdavid.machine_vision.yolo.boundingBox.BoundingBoxes
import com.isdavid.machine_vision.yolo.model_wrapper.TflModelWrapper
import com.isdavid.machine_vision.yolo.model_wrapper.YoloOnDetect
import com.isdavid.machine_vision.yolo.model_wrapper.buildFromDisk
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


interface YoloVMDC {
    var onDetect: YoloOnDetect?
    var onLogImage: (bitmap: Bitmap) -> Unit
    val boundingBoxes: State<BoundingBoxes>
    val imageLog: State<Bitmap>
    val tflModelWrapper: TflModelWrapper
}

class YoloVMD(
    context: Context,
    yoloModelSource: YoloModelSource = YoloModelSource(),
    private val coroutineHolder: CoroutineHolderC = CoroutineHolder()
) : YoloVMDC,
    CoroutineHolderC by coroutineHolder {

    private val _boundingBoxes = mutableStateOf(emptyList<BoundingBox>())
    override val boundingBoxes: State<BoundingBoxes>
        get() = _boundingBoxes

    private val _imageLog = mutableStateOf(createTransparent())
    override val imageLog: State<Bitmap>
        get() = _imageLog

    override var onDetect: YoloOnDetect?
        get() = runBlocking {
            tflModelWrapper.onDetect
        }
        set(value) = runBlocking {
            tflModelWrapper.onDetect = value
        }

    override var onLogImage: (bitmap: Bitmap) -> Unit
        get() = {
            tflModelWrapper.onLogImage
        }
        set(value) {
            tflModelWrapper.onLogImage = value
        }

    override val tflModelWrapper
        get() = runBlocking {
            deferredTflModelWrapper.await()
        }

    private val deferredTflModelWrapper: Deferred<TflModelWrapper> = localScope.async {
        val model = TflModelWrapper.buildFromDisk(
            context,
            modulePath = yoloModelSource.modulePath,
            labelsPath = yoloModelSource.labelsPath,
        )
        return@async model
    }

    private var lastInterval = System.currentTimeMillis()

    init {
        localScope.launch {
            onDetect = { sourceBitmap, boundingBoxes, _, _, _ ->
                this@YoloVMD._boundingBoxes.value = boundingBoxes
                sourceBitmap.recycle()
            }

            onLogImage = { log ->
                val current = System.currentTimeMillis()
                val elapsed = current - lastInterval

                if (elapsed > 333){
                    _imageLog.value = BitmapOperations.resizeMaintainingAspectRatio(
                        log,
                        500
                    )
                    lastInterval = current
                }
            }
        }
    }

    override fun clear() = runBlocking {
        tflModelWrapper.close()
        coroutineHolder.clear()
    }

    fun updateBoxes(boundingBoxes: BoundingBoxes) {
        _boundingBoxes.value = boundingBoxes
    }
}


