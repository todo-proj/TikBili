package com.benyq.tikbili.ext

import android.content.res.Resources

/**
 *
 * @author benyq
 * @date 8/9/2023
 *
 */

/**
 * dp to px
 */
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.px: Float
    get() = this * Resources.getSystem().displayMetrics.density