package com.ritterdouglas.overlayslib.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.DisplayMetrics

object BitmapUtils {

    fun createTransparentBitmapFromBitmap(bitmap: Bitmap?,
                                          replaceThisColor: Int): Bitmap? {
        if (bitmap != null) {
            val picw = bitmap.width
            val pich = bitmap.height
            val pix = IntArray(picw * pich)
            bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich)

            for (y in 0..pich - 1) {
                // from left to right
                for (x in 0..picw - 1) {
                    val index = y * picw + x
                    val r = pix[index] shr 16 and 0xff
                    val g = pix[index] shr 8 and 0xff
                    val b = pix[index] and 0xff

                    if (pix[index] == replaceThisColor) {
                        pix[index] = Color.TRANSPARENT
                    } else {
                        break
                    }
                }

                // from right to left
                for (x in picw - 1 downTo 0) {
                    val index = y * picw + x
                    val r = pix[index] shr 16 and 0xff
                    val g = pix[index] shr 8 and 0xff
                    val b = pix[index] and 0xff

                    if (pix[index] == replaceThisColor) {
                        pix[index] = Color.TRANSPARENT
                    } else {
                        break
                    }
                }
            }

            val bm = Bitmap.createBitmap(pix, picw, pich,
                    Bitmap.Config.ARGB_4444)

            return bm
        }
        return null
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return flip(Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true))
    }

    fun flip(bitmap: Bitmap): Bitmap {
        val m = Matrix()
        m.preScale(-1f, 1f)
        val dst = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, false)
        dst.density = DisplayMetrics.DENSITY_DEFAULT
        return dst
    }
}
