package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

import android.support.v4.content.res.ResourcesCompat
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.graphics.drawable.Drawable

class FaceOverlay : View{

    companion object { val TAG = FaceOverlay::class.simpleName }

    var mImage: Drawable? = null
    var mActivePointerId = INVALID_POINTER_ID
    var mScaleDetector: ScaleGestureDetector? = null

    var mPosX: Float = 0f
    var mPosY: Float = 0f
    var mLastTouchX: Float = 0f
    var mLastTouchY: Float = 0f

    var mScaleFactor = 1f

    var imageResourceId: Int? = null
    var isCurrentlyVisible: Boolean = false
    var isCurrentlySelected: Boolean = false

    constructor(context: Context) : super(context) { init() }

    constructor(context: Context, resourceId: Int) : super(context) {
        this.imageResourceId = resourceId
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init() }

    private fun init() {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        setupImageInitialState()
    }

    fun setupImageInitialState() {
        mImage = ResourcesCompat.getDrawable(resources, imageResourceId!!, null)
        mImage!!.setBounds(0, 0, mImage!!.intrinsicWidth/2, mImage!!.intrinsicHeight/2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isCurrentlyVisible) {
            canvas.save()
            Log.d("DEBUG", "X: "+mPosX+" Y: "+mPosY)
            canvas.translate(mPosX, mPosY)
            canvas.scale(mScaleFactor, mScaleFactor)
            mImage!!.draw(canvas)
            canvas.restore()
        }
    }

    fun setCurrent(current: Boolean) {
        this.isCurrentlyVisible = current
        invalidate()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (isCurrentlySelected) {
            mScaleDetector!!.onTouchEvent(ev)

            val action = ev.getAction()
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val x = ev.getX()
                    val y = ev.getY()

                    mLastTouchX = x
                    mLastTouchY = y
                    mActivePointerId = ev.getPointerId(0)
                }

                MotionEvent.ACTION_MOVE -> {
                    val pointerIndex = ev.findPointerIndex(mActivePointerId)
                    val x = ev.getX(pointerIndex)
                    val y = ev.getY(pointerIndex)

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    if (!mScaleDetector!!.isInProgress()) {
                        val dx = x - mLastTouchX
                        val dy = y - mLastTouchY

                        mPosX += dx
                        mPosY += dy

                        invalidate()
                    }

                    mLastTouchX = x
                    mLastTouchY = y
                }

                MotionEvent.ACTION_UP -> {
                    mActivePointerId = INVALID_POINTER_ID
                }

                MotionEvent.ACTION_CANCEL -> {
                    mActivePointerId = INVALID_POINTER_ID
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerIndex = ev.getAction() and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                    val pointerId = ev.getPointerId(pointerIndex)
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        val newPointerIndex = if (pointerIndex == 0) 1 else 0
                        mLastTouchX = ev.getX(newPointerIndex)
                        mLastTouchY = ev.getY(newPointerIndex)
                        mActivePointerId = ev.getPointerId(newPointerIndex)
                    }
                }
            }
            return true
        } else return super.onTouchEvent(ev)
    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            invalidate()
            return true
        }
    }

    fun viewToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.argb(0, 255, 255, 255))
        val canvas = Canvas(bitmap)
        this.draw(canvas)

        return bitmap

    }


}
