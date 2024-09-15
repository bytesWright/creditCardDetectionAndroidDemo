package com.isdavid.machine_vision.yolo.boundingBox

import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.TensorProperties

fun computeBoundingBoxesX(
    tensorOutput: FloatArray, tp: TensorProperties, confidenceThreshold: Float = .3F, labels: List<String>
): DetectionBoundingBoxes {
    val detectionBoundingBoxes = mutableListOf<DetectionBoundingBox>()

    for (c in 0 until tp.predictionsLimit) {
        var confidence = -1.0f
        var maxIdx = -1
        var j = 4

        var arrayIdx = c + tp.predictionsLimit * j

        while (j < tp.dataBundleSize) {
            if (tensorOutput[arrayIdx] > confidence) {
                confidence = tensorOutput[arrayIdx]
                maxIdx = j - 4
            }
            j++
            arrayIdx += tp.predictionsLimit
        }

        if (confidence > confidenceThreshold) {
            val clsName = labels[maxIdx]
            val cx = tensorOutput[c] // 0
            val cy = tensorOutput[c + tp.predictionsLimit] // 1
            val w = tensorOutput[c + tp.predictionsLimit * 2]
            val h = tensorOutput[c + tp.predictionsLimit * 3]

            val x1 = cx - (w / 2F)
            val y1 = cy - (h / 2F)
            val x2 = cx + (w / 2F)
            val y2 = cy + (h / 2F)

            if (x1 < 0F || x1 > 1F) continue
            if (y1 < 0F || y1 > 1F) continue
            if (x2 < 0F || x2 > 1F) continue
            if (y2 < 0F || y2 > 1F) continue

            detectionBoundingBoxes.add(
                DetectionBoundingBox(
                    x1 = x1,
                    y1 = y1,
                    x2 = x2,
                    y2 = y2,
                    centerX = cx,
                    centerY = cy,
                    width = w,
                    height = h,
                    confidence = confidence,
                    classId = maxIdx,
                    className = clsName
                )
            )
        }
    }

    return detectionBoundingBoxes
}


fun computeBoundingBoxes(
    tensorOutput: FloatArray, tp: TensorProperties, confidenceThreshold: Float = .3F, labels: List<String>
): DetectionBoundingBoxes {
    val detectionBoundingBoxes = mutableListOf<DetectionBoundingBox>()

    for (c in 0 until tp.predictionsLimit) {
        val predictionPointer = c * tp.dataBundleSize
        val bundleLimit = predictionPointer + tp.dataBundleSize

        val classesConfidenceValue = tensorOutput.sliceArray(predictionPointer + 4 until bundleLimit)
        val maxElementWithIndex = classesConfidenceValue.withIndex().maxBy { it.value }
        val confidence = maxElementWithIndex.value
        val classId = maxElementWithIndex.index

        if (confidence > confidenceThreshold) {
            val boundingBox = buildBoundingBox(tensorOutput, predictionPointer, confidence, classId, labels) ?: continue
            detectionBoundingBoxes.add(boundingBox)
        }
    }

    return detectionBoundingBoxes
}

private fun buildBoundingBox(
    tensorOutput: FloatArray,
    predictionPointer: Int,
    confidence: Float,
    classId: Int,
    labels: List<String>,
): DetectionBoundingBox? {
    val cx = tensorOutput[predictionPointer]
    val cy = tensorOutput[predictionPointer + 1]
    val w = tensorOutput[predictionPointer + 2]
    val h = tensorOutput[predictionPointer + 3]

    val x1 = cx - (w / 2F)
    val y1 = cy - (h / 2F)
    val x2 = cx + (w / 2F)
    val y2 = cy + (h / 2F)

    if (x1 < 0F || x1 > 1F) return null
    if (y1 < 0F || y1 > 1F) return null
    if (x2 < 0F || x2 > 1F) return null
    if (y2 < 0F || y2 > 1F) return null

    return DetectionBoundingBox(
        x1 = x1,
        y1 = y1,
        x2 = x2,
        y2 = y2,
        centerX = cx,
        centerY = cy,
        width = w,
        height = h,
        confidence = confidence,
        classId = classId,
        className = labels[classId]
    )
}

