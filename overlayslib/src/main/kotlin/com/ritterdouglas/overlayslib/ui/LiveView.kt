package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

import com.ritterdouglas.overlayslib.R
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.widget.LinearLayout
import android.view.ViewTreeObserver



class LiveView : FrameLayout, Camera.FaceDetectionListener {


    companion object val TAG = LiveView::class.simpleName

    private var mExampleString: String = "LiveView"
    private var mExampleColor = Color.RED
    private var mExampleDimension = 0f

    var exampleDrawable: Drawable? = null

    private var mTextPaint: TextPaint? = null
    private var mTextWidth: Float = 0.toFloat()
    private var mTextHeight: Float = 0.toFloat()

    var cameraPreview: FrameLayout? = null
    var listener: OverlaysCallbacks? = null
    var anOverlay: FaceOverlay? = null

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

        if (camera != null)
            cameraPreview?.addView(CameraPreview(context, camera))
        else listener?.onError("We've had a problem with camera :(")

        val attributes = context.obtainStyledAttributes(
                attrs, R.styleable.LiveView, defStyle, 0)

    }

    private fun invalidateTextPaintAndMeasurements() {
        mTextPaint!!.textSize = mExampleDimension
        mTextPaint!!.color = mExampleColor
        mTextWidth = mTextPaint!!.measureText(mExampleString)

        val fontMetrics = mTextPaint!!.fontMetrics
        mTextHeight = fontMetrics.bottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        /*// Draw the text.
        canvas.drawText(mExampleString!!,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint!!)

        // Draw the example drawable on top of the text.
        if (exampleDrawable != null) {
            exampleDrawable!!.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight)
            exampleDrawable!!.draw(canvas)
        }*/
    }

    var exampleColor: Int
        get() = mExampleColor
        set(exampleColor) {
            mExampleColor = exampleColor
            invalidateTextPaintAndMeasurements()
        }

    var exampleDimension: Float
        get() = mExampleDimension
        set(exampleDimension) {
            mExampleDimension = exampleDimension
            invalidateTextPaintAndMeasurements()
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
        var c: Camera? = null
        try {
            c = if (Camera.getNumberOfCameras() > 1) Camera.open(1) else Camera.open()
            setupCamera(c)
        } catch (e: Exception) {
            Log.e(TAG, "CAMERA NOT AVAILABLE (in use or does not exist)")
        }

        return c
    }

    fun setupCamera(camera: Camera) {
        camera.setDisplayOrientation(90)
        camera.setFaceDetectionListener(this)

        val sizes = camera.parameters.getSupportedPreviewSizes()
        val optimalSize = getOptimalPreviewSize(sizes, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)

        camera.parameters.setPreviewSize(optimalSize!!.height, optimalSize.height)

    }

    override fun onFaceDetection(faces: Array<out Camera.Face>?, camera: Camera?) {
        if (faces!!.isNotEmpty()){
            Log.e("FaceDetection", "face detected: "+ faces.size
                    + " Face 1 Location X: " + faces[0].rect.centerX() +
                    "Y: " + faces[0].rect.centerY() + faces[0].rect.toString())

            if (anOverlay == null) {
                anOverlay = FaceOverlay(context)
                cameraPreview?.addView(anOverlay)
            }

            anOverlay?.rectChanged(faces[0].rect)
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

}
