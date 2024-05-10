package com.benyq.tikbili.base.ext

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

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

val Float.sp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)