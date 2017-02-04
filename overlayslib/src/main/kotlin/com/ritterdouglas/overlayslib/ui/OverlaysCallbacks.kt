package com.ritterdouglas.overlayslib.ui

import android.graphics.Bitmap

interface OverlaysCallbacks {
    fun onError(error: String)
    fun onImageResult(image: Bitmap)

}