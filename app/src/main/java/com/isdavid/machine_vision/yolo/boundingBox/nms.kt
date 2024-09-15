package com.isdavid.machine_vision.yolo.boundingBox

fun filterWithNms(boxes: DetectionBoundingBoxes, iouThreshold: Float = .5F): MutableBoundingBoxes {
    val sortedBoxes = boxes.sortedByDescending { it.confidence }.toMutableList()
    val selectedBoxes = mutableListOf<DetectionBoundingBox>()

    while (sortedBoxes.isNotEmpty()) {
        val first = sortedBoxes.first()
        selectedBoxes.add(first)
        sortedBoxes.remove(first)

        val iterator = sortedBoxes.iterator()
        while (iterator.hasNext()) {
            val nextBox = iterator.next()
            val iou = calculateIoU(first, nextBox)
            if (iou >= iouThreshold) {
                iterator.remove()
            }
        }
    }

    return selectedBoxes
}

fun calculateIoU(box1: DetectionBoundingBox, box2: DetectionBoundingBox): Float {
    val x1 = maxOf(box1.x1, box2.x1)
    val y1 = maxOf(box1.y1, box2.y1)
    val x2 = minOf(box1.x2, box2.x2)
    val y2 = minOf(box1.y2, box2.y2)

    val intersectionArea = maxOf(0F, x2 - x1) * maxOf(0F, y2 - y1)

    val box1Area = box1.width * box1.height
    val box2Area = box2.width * box2.height
    return intersectionArea / (box1Area + box2Area - intersectionArea)
}