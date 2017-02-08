package com.ritterdouglas.overlays

import android.graphics.Bitmap

interface MainView {

    fun initLiveView()
    fun showStartLibButton(show: Boolean)
    fun showDescription(show: Boolean)
    fun setupOptions()
    fun showToolbar(show: Boolean)
    fun hideStatusBar()
    fun showPictureButton(show: Boolean)
    fun showResultContainer(show: Boolean)
    fun setResultImage(bitmap: Bitmap)
    fun showOptionsContainer(show: Boolean)
    fun handlePermissions()
    fun onError(error: String)

}