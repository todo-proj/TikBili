package com.benyq.tikbili.ui.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.animation.addListener
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
    private var onStateChangedListener: OnStateChangedListener? = null

    init {
        orientation = VERTICAL
    }

    fun setOnStateChangedListener(listener: OnStateChangedListener) {
        onStateChangedListener = listener
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        contentChild = getChildAt(0)
        bottomChild = getChildAt(1)
    }

    fun canCloseComment(): Boolean {
        return contentChild.height != height && !(touchReleaseAnimator?.isRunning ?: false)
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
            addListener(onStart = {
                if (it.duration != 0L) {
                    onStateChangedListener?.onStateChanged(State.STATE_OPEN)
                }
            })
            start()
        }
    }


    fun close() {
        openAnimator?.duration = 0
        openAnimator?.cancel()
        touchReleaseAnimator?.duration = 0
        touchReleaseAnimator?.cancel()

        touchReleaseAnimator = ValueAnimator.ofInt(contentChild.height, height).apply {
            duration = 300
            addUpdateListener {
                contentChild.updateLayoutParams<LayoutParams> { height = it.animatedValue as Int }
            }
            addListener(onStart = {
                if (it.duration != 0L) {
                    onStateChangedListener?.onStateChanged(State.STATE_CLOSE)
                }
            })
            start()
        }
    }

    private var lastDy = 0f
    private var disY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastDy = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                disY = ev.rawY - lastDy
                lastDy = ev.rawY
                Log.d(TAG, "dispatchTouchEvent: disY: $disY")
                //判断是否拦截阻止事件被拦截
                if (contentChild.height < height) {
                    requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        helper.onNestedScrollAccepted(child, target, axes, type)
        closeAnimator()
        requestDisallowInterceptTouchEvent(true)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        helper.onStopNestedScroll(target, type)
        if (contentChild.height > height / 2) {
            close()
        }else {
            open()
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

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {

    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.d(TAG, "onNestedPreScroll: target: $target. canScrollVertically: ${bottomChild.canScrollVertically(-1)}")
        if (!bottomChild.canScrollVertically(-1)) {
            //向上
            Log.d(TAG, "dispatchTouchEvent: dy: $dy")
            var consume = dy
            if (disY > 0) {
                //向下
                if (dy < 0) {
                    contentChild.updateLayoutParams<LayoutParams> { height -= dy }
                }
            }else {
                if (dy > 0) {
                    consume = if (contentChild.height - (measuredHeight / 3 + dy) > 0) dy else contentChild.height - measuredHeight / 3
                    contentChild.updateLayoutParams<LayoutParams> { height -= consume }
                }
            }
            consumed[1] = consume
        }
    }

    private fun closeAnimator() {
        touchReleaseAnimator?.duration = 0
        touchReleaseAnimator?.cancel()

        openAnimator?.duration = 0
        openAnimator?.cancel()
    }
    fun interface OnStateChangedListener {
        fun onStateChanged(state: State)
    }


    enum class State
    {
        STATE_OPEN,
        STATE_CLOSE,
    }


}