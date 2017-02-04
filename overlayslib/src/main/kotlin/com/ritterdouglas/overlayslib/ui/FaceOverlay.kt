package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View

import com.ritterdouglas.overlayslib.R
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth


class FaceOverlay : View {

    companion object { val TAG = FaceOverlay::class.simpleName }

    val paint = Paint()
    var rect: Rect? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) { }

    fun rectChanged(r: Rect) {
        rect = r
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        paint.strokeWidth = 10F

//        canvas.drawRect(rect, paint)

        if (rect != null) {
            /*canvas.save()
            canvas.rotate(90f)
            canvas.drawRect(rect,paint)
            canvas.restore()*/
//            canvas.save()
//            canvas.rotate(90f)

            Log.e(TAG, "ONDRAW")

//            canvas.rotate(90f)

            val rectF = RectF(rect)
            val matrix = Matrix()
            matrix.setScale(1f, 1f)
            matrix.postScale(contentWidth / 2000f, contentHeight / 2000f)
            matrix.postTranslate(contentWidth / 2f, contentWidth / 2f)
            matrix.mapRect(rectF)

            canvas.drawRect(rectF, paint)


//            canvas.restore()


        }



    }


}
