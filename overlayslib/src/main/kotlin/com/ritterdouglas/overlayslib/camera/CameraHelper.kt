package com.ritterdouglas.overlayslib.camera

import android.content.Context
import android.content.pm.PackageManager



class CameraHelper {
    /** Check if this device has a camera  */
    public fun checkCameraHardware(context: Context): Boolean {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true
        } else {
            // no camera on this device
            return false
        }
    }
}