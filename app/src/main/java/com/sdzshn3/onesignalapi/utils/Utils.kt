package com.sdzshn3.onesignalapi.utils

import android.content.Context
import android.graphics.Bitmap
import com.sdzshn3.onesignalapi.R

fun resizeBitmapForLargeIconArea(context: Context, bitmap: Bitmap?): Bitmap? {
    if (bitmap == null) return null
    try {
        val systemLargeIconHeight = context.resources.getDimension(R.dimen.notification_large_icon_height)
                .toInt()
        val systemLargeIconWidth = context.resources.getDimension(R.dimen.notification_large_icon_width)
                .toInt()
        val bitmapHeight = bitmap.height
        val bitmapWidth = bitmap.width
        if (bitmapWidth > systemLargeIconWidth || bitmapHeight > systemLargeIconHeight) {
            var newWidth = systemLargeIconWidth
            var newHeight = systemLargeIconHeight
            if (bitmapHeight > bitmapWidth) {
                val ratio = bitmapWidth.toFloat() / bitmapHeight.toFloat()
                newWidth = (newHeight * ratio).toInt()
            } else if (bitmapWidth > bitmapHeight) {
                val ratio = bitmapHeight.toFloat() / bitmapWidth.toFloat()
                newHeight = (newWidth * ratio).toInt()
            }
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return bitmap
}