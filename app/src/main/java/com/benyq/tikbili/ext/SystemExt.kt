package com.benyq.tikbili.ext

import android.app.Activity
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar

fun Activity.showLoading() {
    contentView()?.apply {
        val pb = ProgressBar(context)
        pb.tag = "pb"
        addView(pb, FrameLayout.LayoutParams(150, 150, Gravity.CENTER))
    }
}

fun Activity.hideLoading() {
    val pb = contentView()?.findViewWithTag<ProgressBar>("pb")
    pb?.let { contentView()?.removeView(it) }
}

fun Activity.contentView(): FrameLayout? {
    return takeIf { !isFinishing && !isDestroyed }?.window?.decorView?.findViewById(android.R.id.content)
}