package com.isdavid.histogram

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.isdavid.machine_vision.camera.CameraData
import com.isdavid.machine_vision.camera.log.cameraDataToStr
import com.isdavid.machine_vision.camera.query.queryCamerasData
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraData {
    private val tag = "QCD"
    private val context: Context = ApplicationProvider.getApplicationContext()


    @Test
    fun testCameraData() {
//        CameraHelper.logSupportedOutputSizes(
//            context,
//            ImageFormat.YUV_420_888,
//            tag = tag
//        )

        val cameraData = CameraData.queryCamerasData(context = context)

        Log.d(tag, cameraData.joinToString(separator = "\n\n") {
            cameraDataToStr(it)
        })
    }
}



