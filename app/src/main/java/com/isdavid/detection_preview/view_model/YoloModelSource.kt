package com.isdavid.detection_preview.view_model

data class YoloModelSource(
    val modulePath: String = "ccod/v0.5/model-float-32.tflite",
    val labelsPath: String = "ccod/v0.5/labels.txt"
)