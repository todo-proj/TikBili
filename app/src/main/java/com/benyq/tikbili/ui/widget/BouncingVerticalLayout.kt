package com.benyq.tikbili.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs

/**
 *
 * @author benyq
 * @date 5/20/2024
 * 写法有问题。没必要用嵌套滑动，直接用touchListener就行
 */
@Deprecated("design defect")
class BouncingVerticalLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    companion object {
        private const val TAG = "BouncingLayout"
        private const val MAX_OVER_SCROLL_PIXELS = 600
    }

    private val helper = NestedScrollingParentHelper(this)
    private var _child: View? = null
    private val scroller = OverScroller(context)


    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            throw IllegalStateException("BouncingLayout can only have one child")
        }
        _child = getChildAt(0)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        helper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (scrollY != 0) {
            scroller.startScroll(0, scrollY, 0, -scrollY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
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

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(target, velocityX, velocityY)
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
        val child = _child ?: return
        if (dy > 0) {
            Log.d(TAG, "onNestedPreScroll 1 : $scrollY, $dy")
            if (scrollY < 0) {
                if (scrollY + dy > 0) {
                    consumed[1] = -scrollY
                }else {
                    consumed[1] = dy
                }
                Log.d(TAG, "onNestedPreScroll 1 : $scrollY, $dy, ${consumed[1]}")
                scrollBy(0, consumed[1])
                return
            }
            if (!child.canScrollVertically(1)) {
                if (scrollY + dy >= MAX_OVER_SCROLL_PIXELS) {
                    consumed[1] = MAX_OVER_SCROLL_PIXELS - scrollY
                }else {
                    consumed[1] = dy
                }
                Log.d(TAG, "onNestedPreScroll 1: $dy, ${consumed[1]}")
                scrollBy(0, consumed[1])
            }
        }else {
            Log.e(TAG, "onNestedPreScroll 2 : $scrollY, $dy")
            if (scrollY > 0) {
                if (scrollY + dy <= 0) {
                    consumed[1] = -scrollY
                }else {
                    consumed[1] = dy
                }
                scrollBy(0, consumed[1])
                return
            }
            if (!child.canScrollVertically(-1)) {
                // 向上滚动
                if (abs(scrollY + dy) >= MAX_OVER_SCROLL_PIXELS) {
                    consumed[1] = -MAX_OVER_SCROLL_PIXELS - scrollY
                }else {
                    consumed[1] = dy
                }
                Log.e(TAG, "onNestedPreScroll2: $dy, ${consumed[1]}, $scrollY")
                scrollBy(0, consumed[1])
            }
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.e(TAG, "onNestedPreScroll3: $scrollY")
                if (scrollY != 0) {
                    scroller.startScroll(0, scrollY, 0, -scrollY)
                    ViewCompat.postInvalidateOnAnimation(this)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }



}