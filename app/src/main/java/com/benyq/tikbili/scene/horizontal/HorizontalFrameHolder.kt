package com.benyq.tikbili.scene.horizontal

import android.graphics.Bitmap

/**
 *
 * @author benyq
 * @date 5/8/2024
 *
 */
object HorizontalFrameHolder {

    private var currentFrame: Bitmap? = null

    fun set(bitmap: Bitmap?) {
        currentFrame = bitmap
    }


    fun get(): Bitmap? {
        return currentFrame
    }
}