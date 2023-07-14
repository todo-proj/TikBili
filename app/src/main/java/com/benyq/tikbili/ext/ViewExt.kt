package com.benyq.tikbili.ext

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 *
 * @author benyq
 * @date 7/14/2023
 *
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun ViewPager2.overScrollNever() {
    val child: View = getChildAt(0)
    (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
}
