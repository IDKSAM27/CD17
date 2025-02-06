package com.example.cd_17

import android.content.Context
import android.graphics.Bitmap
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class ObjectDetector(context: Context) {
    private var model: Module

    init {
        val modelPath = Utils.assetFilePath(context, "yolov5s.torchscript.pt")
        model = Module.load(modelPath)
    }

    fun runModel(bitmap: Bitmap): Tensor {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        return model.forward(IValue.from(inputTensor)).toTensor()
    }
}
