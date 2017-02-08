package com.ritterdouglas.overlays

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.ritterdouglas.overlayslib.ui.LiveView
import com.ritterdouglas.overlayslib.ui.OverlayOptionView
import com.ritterdouglas.overlayslib.ui.OverlaysCallbacks
import android.view.WindowManager
import android.os.Build

class MainActivity : AppCompatActivity(), MainView, OverlaysCallbacks {
    companion object {
        val TAG = MainActivity::class.simpleName
        val MY_PERMISSIONS_REQUEST_CAMERA = 100
    }

    val toolbar by lazy { findViewById(R.id.toolbar) as Toolbar? }
    val uiLibComponent by lazy { findViewById(R.id.cameraContent) as FrameLayout? }
    val startLibButton by lazy { findViewById(R.id.startLibButton) as Button? }
    val takePictureButton by lazy { findViewById(R.id.takePictureButton) as FloatingActionButton? }
    val resultContainer by lazy { findViewById(R.id.resultContainer) as RelativeLayout? }
    val resultImage by lazy { findViewById(R.id.resultImage) as ImageView? }
    val description by lazy { findViewById(R.id.description) as TextView? }
    val optionsContainer by lazy { findViewById(R.id.overlayButtonsContainer) as LinearLayout? }

    var liveView: LiveView? = null
    var presenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter = MainPresenter()
        presenter?.view = this

        startLibButton?.setOnClickListener { handlePermissions() }
        takePictureButton?.setOnClickListener { liveView?.takePicture() }

    }

    override fun handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
        } else presenter?.startLib()

    }

    override fun initLiveView() {
        liveView = LiveView(this, this)
        uiLibComponent?.addView(liveView)
    }

    override fun showStartLibButton(show: Boolean) {
        startLibButton?.visibility = if (show) VISIBLE else GONE
    }

    override fun showDescription(show: Boolean) {
        description?.visibility = if (show) VISIBLE else GONE
    }

    override fun setupOptions() {
        for ((index, value) in liveView?.getImageChoices()!!.withIndex()) {
            val view = OverlayOptionView(this, value, null)
            view.setOnClickListener { handleOptionClick(index) }
            optionsContainer?.addView(view)
        }
    }

    override fun showToolbar(show: Boolean) {
        toolbar?.visibility = if (show) VISIBLE else GONE
    }

    override fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun showPictureButton(show: Boolean) {
        takePictureButton?.visibility = if (show) VISIBLE else GONE
    }

    override fun showResultContainer(show: Boolean) {
        resultContainer?.visibility = if (show) VISIBLE else GONE
    }

    override fun setResultImage(bitmap: Bitmap) {
        resultImage?.setImageBitmap(bitmap)
    }

    override fun showOptionsContainer(show: Boolean) {
        optionsContainer?.visibility = if (show) VISIBLE else GONE
    }


    fun handleOptionClick(position: Int) {
        liveView?.changedOptionPosition(liveView!!.getOverlayPosition(position))
        for (item in 0..optionsContainer?.childCount!!-1) {
            val view = optionsContainer?.getChildAt(item) as OverlayOptionView
            if (position == item) view.highlightView(true) else view.highlightView(false)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter?.handlePermissionsResult(requestCode, grantResults)
    }

    override fun onError(error: String) {
        Log.e(TAG, error)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onImageResult(image: Bitmap) { presenter?.imageReceived(image) }

    override fun faceRecognized() { presenter?.faceRecognized() }

    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }


}
