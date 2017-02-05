package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

import java.io.IOException

class CameraPreview(context: Context, private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    companion object val TAG = CameraPreview::class.java.simpleName

    val mHolder: SurfaceHolder

    init {
        mHolder = holder
        mHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            mCamera.setPreviewDisplay(holder)
            mCamera.startPreview()
            startDetectingFaces()
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera.release()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        if (mHolder.surface == null) {
            return
        }

        try {
            mCamera.stopPreview()
        } catch (e: Exception) {

        }

        try {
            mCamera.setPreviewDisplay(mHolder)
            mCamera.startPreview()
            startDetectingFaces()

        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }
    }

    fun startDetectingFaces() {
        val params = mCamera.parameters

        if (params.maxNumDetectedFaces > 0) {
            mCamera.startFaceDetection()
        } else {
            Log.e(TAG, "CAMERA DOESN'T SUPPORT FACE DETECTION :(")
        }
    }


}