package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

import com.ritterdouglas.overlayslib.R
import android.widget.ImageView
import android.graphics.*


class LiveView : FrameLayout, Camera.FaceDetectionListener {

    companion object {
        val TAG = LiveView::class.simpleName
        val NUMBER_OF_CHILDS_BEFORE_OVERLAYS = 2
    }

    var mCamera: Camera? = null
    var cameraPreview: FrameLayout? = null
    var listener: OverlaysCallbacks? = null
    var anOverlay: FaceOverlay? = null
    var resultImage: ImageView? = null

    var currentOverlay: Int? = 2
    var atLeastOneFaceRecognized = false

    constructor(context: Context, listener: OverlaysCallbacks) : super(context) {
        init(context, null, 0)
        this.listener = listener
    }

    constructor(context: Context) : super(context) { init(context, null, 0) }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(context, attrs, 0) }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs, defStyle) }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        View.inflate(context, R.layout.live_view, this)

        cameraPreview = findViewById(R.id.cameraPreview) as FrameLayout?
        var camera = checkAndInitCamera()
        resultImage = ImageView(context)

        if (camera != null) {
            cameraPreview?.addView(CameraPreview(context, camera))
            cameraPreview?.addView(resultImage)
            createOverlays()
        } else listener?.onError("We've had a problem with camera :(")

    }

    fun createOverlays() {
        if (cameraPreview?.childCount!! < NUMBER_OF_CHILDS_BEFORE_OVERLAYS + 1) {
            Log.e(TAG, "CREATE OVERLAYS !!!!!!!!!!!!")
            getImageChoices()
                    .map { FaceOverlay(context, it) }
                    .forEach { cameraPreview?.addView(it) }
        }
    }

    override fun onDraw(canvas: Canvas) { super.onDraw(canvas) }

    fun changedOptionPosition(position: Int) {
        for (item in NUMBER_OF_CHILDS_BEFORE_OVERLAYS..cameraPreview!!.childCount-1) {
            val child = cameraPreview?.getChildAt(item) as FaceOverlay?
            if (item == position) {
                child?.setCurrent(true)
            }
            child?.isCurrentlySelected = item == position
        }
    }

    fun checkAndInitCamera(): Camera? {
        if (checkCameraHardware(context))
            return getCameraInstance()
        else return null
    }

    fun checkCameraHardware(context: Context): Boolean {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(TAG, "CAMERA HAS FEATURE")
            return true
        } else {
            Log.e(TAG, "NO CAMERA ON THIS DEVICE")
            return false
        }
    }

    fun getCameraInstance(): Camera? {
        try {
            mCamera = if (Camera.getNumberOfCameras() > 1) Camera.open(1) else Camera.open()
            setupCamera()
        } catch (e: Exception) {
            Log.e(TAG, "CAMERA NOT AVAILABLE (in use or does not exist)")
        }

        return mCamera
    }

    fun setupCamera() {
        mCamera?.setDisplayOrientation(90)
        mCamera?.setFaceDetectionListener(this)

        val sizes = mCamera?.parameters?.supportedPreviewSizes
        val optimalSize = getOptimalPreviewSize(sizes, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)

        mCamera?.parameters?.setPreviewSize(optimalSize!!.height, optimalSize.height)

    }

    override fun onFaceDetection(faces: Array<out Camera.Face>?, camera: Camera?) {
        if (faces!!.isNotEmpty()) {
            Log.d("FaceDetection", "face detected: "+ faces.size
                    + " Face 1 Location X: " + faces[0].rect.centerX() +
                    "Y: " + faces[0].rect.centerY() + faces[0].rect.toString() + " bottom: "+faces[0].rect.bottom)

            atLeastOneFaceRecognized = true
            createOverlays()
            listener?.faceRecognized()
        }
    }

    fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.05
        val targetRatio = w.toDouble() / h

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE
        val targetHeight = h

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }

        Log.e(TAG, "optimal size: width: " + optimalSize?.width+ " height: "+optimalSize?.height)

        return optimalSize

    }

    fun takePicture() {
        mCamera?.takePicture(null, null, mPicture)
    }

    val mPicture = Camera.PictureCallback { data, camera ->
        mCamera?.stopPreview()
        mCamera?.release()
        val bMap = BitmapFactory.decodeByteArray(data, 0, data.size)
        resultImage?.setImageBitmap(BitmapUtils.rotateImage(bMap, -90f))

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        cameraPreview?.draw(canvas)

        listener?.onImageResult(bitmap)
    }

    fun getImageChoices() = arrayOf(R.drawable.trump_face, R.drawable.trump_hair, R.drawable.trump_hand)

    fun getOverlayPosition(position: Int) = position + 2

}
