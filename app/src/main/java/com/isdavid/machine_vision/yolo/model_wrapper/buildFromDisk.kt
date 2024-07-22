package com.isdavid.machine_vision.yolo.model_wrapper

import android.content.Context
import android.util.Log
import com.isdavid.common.readFileAsLines
import com.isdavid.machine_vision.yolo.model_wrapper.tensorflow.GpuStatus
import org.tensorflow.lite.support.common.FileUtil

fun TflModelWrapper.Companion.buildFromDisk(
    context: Context,
    modulePath: String,
    labelsPath: String
): TflModelWrapper {
    val model: TensorflowRawModel = FileUtil.loadMappedFile(context, modulePath)
    val labels: MutableList<String> = readFileAsLines(context, labelsPath)
    val gpuAvailable = GpuStatus(context).available

    return TflModelWrapper(
        model,
        labels,
        gpuAvailable
    )
}