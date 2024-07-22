package com.isdavid

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.isdavid.cameraUtils.yoloV8.YoloV8ModelWrapper
import com.isdavid.cameraUtils.yoloV8.boundingBox.BoundingBox

class DetectionViewModel(context: Context) : ViewModel() {
    private val _boundingBoxes = mutableStateOf(emptyList<BoundingBox>())

    val boundingBoxes: State<List<BoundingBox>>
        get() = _boundingBoxes

    val cardYoloV8ModelWrapper =
        YoloV8ModelWrapper(
            context,
            "yoloV8/model16.tflite",
            "yoloV8/labels.txt",
        ) { _, boundingBoxes, _ ->
            _boundingBoxes.value = boundingBoxes
        }

}


