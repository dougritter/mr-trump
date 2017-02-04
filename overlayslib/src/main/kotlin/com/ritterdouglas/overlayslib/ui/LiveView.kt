package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.content.pm.PackageManager
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

class LiveView : FrameLayout {

    companion object val TAG = LiveView::class.simpleName

    private var mExampleString: String = "LiveView"
    private var mExampleColor = Color.RED
    private var mExampleDimension = 0f

    var exampleDrawable: Drawable? = null

    private var mTextPaint: TextPaint? = null
    private var mTextWidth: Float = 0.toFloat()
    private var mTextHeight: Float = 0.toFloat()

    var cameraPreview: FrameLayout? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        // Load attributes

        View.inflate(context, R.layout.live_view, this)

        cameraPreview = findViewById(R.id.cameraPreview) as FrameLayout?
        var camera = checkAndInitCamera()

        if (camera != null)
            cameraPreview?.addView(CameraPreview(context, camera))

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
            c = Camera.open()
        } catch (e: Exception) {
            Log.e(TAG, "CAMERA NOT AVAILABLE (in use or does not exist");
        }

        return c
    }


}
