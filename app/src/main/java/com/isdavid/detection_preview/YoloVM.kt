package com.isdavid.detection_preview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.isdavid.common.view_model.VMF
import com.isdavid.detection_preview.view_model.YoloVMD
import com.isdavid.detection_preview.view_model.YoloVMDC

class YoloVMF(private val app: Application) : VMF<YoloVM>() {
    override fun build(): YoloVM = YoloVM(app)
}

class YoloVM(
    app: Application,
    private val yoloVMD: YoloVMD = YoloVMD(app.baseContext),
) : AndroidViewModel(app),
    YoloVMDC by yoloVMD {
    override fun onCleared() {
        super.onCleared()
        yoloVMD.clear()
    }
}

