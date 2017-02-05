package com.ritterdouglas.overlayslib.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.ritterdouglas.overlayslib.R

class OverlayOptionView : RelativeLayout {

    companion object {
        val TAG = OverlayOptionView::class.simpleName
    }

    var image: ImageView? = null
    var title: TextView? = null
    var highlight: View? = null
    var resourceId: Int? = null
    var titleText: String? = null

    constructor(context: Context, resourceId: Int, title: String?) : super(context) {
        this.resourceId = resourceId
        this.titleText = title
        init()
    }

    constructor(context: Context) : super(context) { init() }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init() }

    private fun init() {
        View.inflate(context, R.layout.overlay_option_view, this)

        image = findViewById(R.id.image) as ImageView
        title = findViewById(R.id.title) as TextView
        highlight = findViewById(R.id.highlightView) as View

        image?.setImageResource(resourceId!!)
        if (titleText != null) title?.text = titleText

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    fun highlightView(highlight: Boolean) = if (highlight) this.highlight?.visibility = VISIBLE else this.highlight?.visibility = View.GONE
}
