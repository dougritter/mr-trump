package com.ritterdouglas.overlays

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.view.View.GONE
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.ritterdouglas.overlayslib.ui.LiveView

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.simpleName
        val MY_PERMISSIONS_REQUEST_CAMERA = 100
    }

    val uiLibComponent by lazy { findViewById(R.id.cameraContent) as FrameLayout? }
    val startLibButton by lazy { findViewById(R.id.startLibButton) as Button? }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLibButton?.setOnClickListener { handlePermissions() }

    }

    fun handlePermissions() {

        var array: Array<String> = arrayOf(Manifest.permission.CAMERA)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, array, MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            uiLibComponent?.addView(LiveView(this))
            startLibButton?.visibility = GONE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
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



}
