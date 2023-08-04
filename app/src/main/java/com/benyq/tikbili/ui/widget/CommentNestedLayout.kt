package com.benyq.tikbili.ui.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.animation.doOnCancel
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams

/**
 *
 * @author benyq
 * @date 7/27/2023
 * 实现评论区和视频的交互
 */
class CommentNestedLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    companion object {
        private const val TAG = "CommentNestedLayout"
    }

    private val helper = NestedScrollingParentHelper(this)
    private lateinit var contentChild: View
    private lateinit var bottomChild: View
    private var touchReleaseAnimator: Animator? = null
    private var openAnimator: Animator? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentChild = getChildAt(0)
        bottomChild = getChildAt(1)
    }


    fun open() {
        val viewHeight = height
        val childHeight = contentChild.height
        openAnimator?.duration = 0
        openAnimator?.cancel()

        openAnimator = ValueAnimator.ofInt(childHeight, viewHeight / 3).apply {
            duration = 300
            addUpdateListener {
                contentChild.updateLayoutParams<LayoutParams> { height = it.animatedValue as Int }
            }
            start()
        }
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        helper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        helper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (!bottomChild.canScrollVertically(-1)) {
            Log.d(TAG, "onNestedPreScroll: canScrollVertically: $dy")
            contentChild.updateLayoutParams<LayoutParams> { height -= dy }
        }
    }

}