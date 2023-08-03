package com.benyq.tikbili.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

/**
 *
 * @author benyq
 * @date 7/25/2023
 * 下拉更多, 用基础的事件分发和ViewDragHelper实现
 */
class LoadMoreLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    private var slideBottomListener: OnSlideBottomListener? = null
    private var loadingListener: OnLoadingListener? = null

    private var viewHeight = 0

    private lateinit var contentChild: View
    private lateinit var loadingChild: View
    private val viewDragHelper: ViewDragHelper

    private var touchX = 0f
    private var touchY = 0f

    private var loadState = LoadingState.IDLE
    private val bottomExpand = 100

    private var distanceY = 0

    private val viewDragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == contentChild
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            Log.d("RefreshView", "onViewPositionChanged: $top")
            if (top < 0) {
                startLoading()
            }
            distanceY += dy
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var newTop = top
            if (top > 0) {
                newTop = 0
            }
            if (top < -(loadingChild.measuredHeight + bottomExpand)) {
                newTop = -(loadingChild.measuredHeight + bottomExpand)
            }
            return newTop
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (yvel > 0) {
                //向下fling，关闭
                close(false)
                loadState = LoadingState.IDLE
            }else if (loadState == LoadingState.LOADING) {
                close()
            }
        }

    }

    init {
        viewDragHelper = ViewDragHelper.create(this, viewDragCallback)
    }

    fun setOnSlideBottomListener(slideBottomListener: OnSlideBottomListener) {
        this.slideBottomListener = slideBottomListener
    }

    fun setOnLoadingListener(loadingListener: OnLoadingListener) {
        this.loadingListener = loadingListener
    }

    fun finishLoading() {
        if (loadState != LoadingState.IDLE) {
            loadState = LoadingState.IDLE
            if (contentChild.top != 0) {
                viewDragHelper.smoothSlideViewTo(contentChild, 0, 0)
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    fun startLoading() {
        if (loadState != LoadingState.LOADING) {
            loadState = LoadingState.LOADING
            //do something
            loadingListener?.onLoading()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentChild = getChildAt(1)
        loadingChild = getChildAt(0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewHeight = h
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = ev.x
                touchY = ev.y
                viewDragHelper.processTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                val difX = ev.x - touchX
                val difY = ev.y - touchY
                if (abs(difY) > abs(difX)) {
                    if (loadState == LoadingState.LOADING) {
                        //加载状态，拦截
                        intercept = true
                    }else {
                        val isBottom = slideBottomListener?.slideBottom() ?: false
//                        val isBottom = contentChild.canScrollVertically(-1)
                        if (isBottom) {
                            //向下滑动不拦截，向上拦截
                            intercept = difY < 0
                        }
                    }
                }
            }
        }
        return intercept && viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        loadingChild.layout(0, viewHeight - loadingChild.measuredHeight, loadingChild.measuredWidth, viewHeight)
        contentChild.layout(0, distanceY, contentChild.measuredWidth, contentChild.measuredHeight + distanceY)
    }


    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun close(withExpand: Boolean = true) {
        val finalY = if (withExpand) 0 - loadingChild.measuredHeight else 0
        viewDragHelper.smoothSlideViewTo(contentChild, 0, finalY)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun interface OnSlideBottomListener {
        fun slideBottom(): Boolean
    }

    fun interface OnLoadingListener {
        fun onLoading()
    }

    enum class LoadingState {
        LOADING, IDLE
    }
}