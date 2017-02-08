package com.ritterdouglas.overlays

import android.content.pm.PackageManager
import android.graphics.Bitmap

class MainPresenter {

    companion object { val PERMISSIONS_ERROR = "We didn't receive the permission" }

    var view: MainView? = null

    fun startLib() {
        view?.initLiveView()
        view?.showStartLibButton(false)
        view?.showDescription(false)
        view?.setupOptions()
        view?.showToolbar(false)
        view?.hideStatusBar()
    }

    fun imageReceived(bitmap: Bitmap) {
        view?.showPictureButton(false)
        view?.showResultContainer(true)
        view?.setResultImage(bitmap)
        view?.showOptionsContainer(false)
    }

    fun faceRecognized() {
        view?.showOptionsContainer(true)
        view?.showPictureButton(true)
    }

    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            MainActivity.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    view?.handlePermissions()
                } else view?.onError(PERMISSIONS_ERROR)
                return
            }
        }
    }

    fun destroy() {
        view = null
    }


}
