package com.ritterdouglas.overlays

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.ritterdouglas.overlayslib.ui.LiveView
import com.ritterdouglas.overlayslib.ui.OverlaysCallbacks

class MainActivity : AppCompatActivity(), OverlaysCallbacks {
    companion object {
        val TAG = MainActivity::class.simpleName
        val MY_PERMISSIONS_REQUEST_CAMERA = 100
    }

    val uiLibComponent by lazy { findViewById(R.id.cameraContent) as FrameLayout? }
    val startLibButton by lazy { findViewById(R.id.startLibButton) as Button? }
    val takePictureButton by lazy { findViewById(R.id.takePictureButton) as FloatingActionButton? }
    val resultContainer by lazy { findViewById(R.id.resultContainer) as RelativeLayout? }
    val resultImage by lazy { findViewById(R.id.resultImage) as ImageView? }

    var liveView: LiveView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLibButton?.setOnClickListener { handlePermissions() }
        takePictureButton?.setOnClickListener { liveView?.takePicture() }

    }

    fun handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            liveView = LiveView(this, this)
            uiLibComponent?.addView(liveView)
            startLibButton?.visibility = GONE
            takePictureButton?.visibility = VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handlePermissions()
                } else {
                    Toast.makeText(this, "We didn't receive the permission", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onError(error: String) {
        Log.e(TAG, error)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

    }

    override fun onImageResult(image: Bitmap) {
        //TODO stop camera to release resources
        takePictureButton?.visibility = GONE
        resultContainer?.visibility = VISIBLE
        resultImage?.setImageBitmap(image)
    }

}
