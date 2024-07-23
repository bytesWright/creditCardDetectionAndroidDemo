package com.isdavid.detection_preview.view_model

data class YoloModelSource(
    val modulePath: String = "ccod/v0.3/model-float-16.tflite",
    val labelsPath: String = "ccod/v0.3/labels.txt"
)