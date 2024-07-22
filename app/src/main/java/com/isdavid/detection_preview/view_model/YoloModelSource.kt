package com.isdavid.detection_preview.view_model

data class YoloModelSource(
    val modulePath: String = "yoloV8/model16.tflite",
    val labelsPath: String = "yoloV8/labels.txt"
)