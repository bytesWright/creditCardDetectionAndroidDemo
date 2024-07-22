package com.isdavid.machine_vision.yolo.bundles

data class IntermediateBitmapData(
    val bitmapData: ByteArray,
    val shape: Array<Int>,
    val cameraStatus: CameraStatus
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntermediateBitmapData

        if (!bitmapData.contentEquals(other.bitmapData)) return false
        if (!shape.contentEquals(other.shape)) return false
        if (cameraStatus != other.cameraStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitmapData.contentHashCode()
        result = 31 * result + shape.contentHashCode()
        result = 31 * result + cameraStatus.hashCode()
        return result
    }
}