package com.isdavid.machine_vision.yolo.boundingBox

typealias BoundingBoxes = List<BoundingBox>
typealias MutableBoundingBoxes = MutableList<BoundingBox>

data class BoundingBox(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val centerX: Float,
    val centerY: Float,
    val width: Float,
    val height: Float,
    val confidence: Float,
    val classId: Int,
    val className: String
)

fun BoundingBox.prettyString(): String {
    return """
        BoundingBox Details:
            Class: $className (ID: $classId)
            Coordinates: ($x1, $y1) to ($x2, $y2)
            Center: ($centerX, $centerY)
            Dimensions: Width = $width, Height = $height
            Confidence: ${"%.2f".format(confidence * 100)}%
    """.trimIndent()
}