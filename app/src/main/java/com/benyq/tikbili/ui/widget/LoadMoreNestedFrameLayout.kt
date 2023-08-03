package com.benyq.tikbili.ui.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat


/**
 *
 * @author benyq
 * @date 8/1/2023
 * 下拉更多, 用 NestedScrollingParent3 实现
 */
class LoadMoreNestedFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    companion object {
        private const val TAG = "LoadMoreFrameLayout"
    }

    private lateinit var contentChild: View
    private lateinit var loadingChild: View

    private var loadingChildHeight = 0
    private var expandY = 0
    private val extraHeight = 130

    private val loadingHeight: Int
        get() = loadingChildHeight + extraHeight

    private val helper = NestedScrollingParentHelper(this)
    private val childHelper = NestedScrollingChildHelper(this)

    private var loadingListener: OnLoadingListener? = null
    fun setOnLoadingListener(loadingListener: OnLoadingListener) {
        this.loadingListener = loadingListener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 2) throw IllegalStateException("LoadMoreFrameLayout can host only two direct child")
        contentChild = getChildAt(1)
        loadingChild = getChildAt(0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        loadingChildHeight = loadingChild.measuredHeight
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
        Log.d(TAG, "onNestedScroll1: $dyConsumed, $dyUnconsumed")
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {
        Log.d(TAG, "onNestedScroll2: $dyConsumed, $dyUnconsumed")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.d(TAG, "onNestedPreScroll: $dy, child scroll: ${contentChild.canScrollVertically(1)}, expandY: $expandY, loadingChildHeight: $loadingChildHeight")
        if (!contentChild.canScrollVertically(1)) {
            //内容不能向下滑动
            val consume = if (dy > 0) {
                if (expandY + dy >= loadingHeight) (loadingHeight - expandY) else dy
            }else {
                if (expandY + dy <= 0) -expandY else dy
            }
            contentChild.translationY -= consume
            consumed[1] = consume
            expandY += consume
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, "onNestedPreFling: $velocityX, $velocityY")
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                removeContentChildAnimator()
            }
            MotionEvent.ACTION_UP -> {
                if (expandY > loadingChildHeight) {
                    val distance = expandY - loadingChildHeight
                    Log.d(TAG, "dispatchTouchEvent: $distance")
                    val animatorY = ObjectAnimator.ofFloat(contentChild, "translationY", -loadingChild.measuredHeight.toFloat())
                    animatorY.duration = 300
                    animatorY.addUpdateListener {
                        Log.d(TAG, "dispatchTouchEvent: ${it.animatedValue}")
                        expandY = -(it.animatedValue as Float).toInt()
                    }
                    animatorY.addListener(onEnd = {
                        contentChild.tag = null
                        if (it.duration != 0L) {
                            loadingListener?.onLoading()
                        }
                    })
                    animatorY.start()
                    contentChild.tag = animatorY
                }else if (expandY < loadingHeight) {
                    //还原
                    finishLoading()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    fun finishLoading() {
        removeContentChildAnimator()
        val animatorY = ObjectAnimator.ofFloat(contentChild, "translationY", 0f)
        animatorY.duration = 300
        animatorY.start()
        animatorY.addUpdateListener {
            expandY = -(it.animatedValue as Float).toInt()
        }
        contentChild.tag = null
    }

    private fun removeContentChildAnimator() {
        val animator = contentChild.tag as? ObjectAnimator
        animator?.duration = 0
        animator?.cancel()
        contentChild.tag = null
    }

    fun interface OnLoadingListener {
        fun onLoading()
    }

}