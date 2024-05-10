package com.benyq.tikbili.base.ext

import android.graphics.Paint

/**
 *
 * @author benyq
 * @date 5/10/2024
 *
 */

fun Paint.textHeight(): Float {
    val fm = fontMetrics
    return fm.descent - fm.ascent + fm.leading
}