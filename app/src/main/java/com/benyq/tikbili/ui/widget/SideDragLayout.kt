package com.benyq.tikbili.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper


/**
 *
 * @author benyq
 * @date 7/19/2023
 * 首页视频左滑右滑出现页面,固定3个子View
 */
class SideDragLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr) {

    private var touchX = 0f
    private var touchY = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private lateinit var leftChild: View
    private lateinit var centerChild: View
    private lateinit var rightChild: View

    private val viewDragHelper: ViewDragHelper
    private var moveDistanceX = 0

    private val viewDragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == leftChild || child == centerChild || child == rightChild
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return centerChild.measuredWidth
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            //边界的限制
            val contentWidth = centerChild.measuredWidth
            val detailWidth = rightChild.measuredWidth
            var newLeft = left
            if (child == centerChild) {
                if (left > 0) newLeft = 0
                if (left < -detailWidth) newLeft = -detailWidth
            } else if (child == rightChild) {
                if (left > contentWidth) newLeft = contentWidth;
                if (left < contentWidth - detailWidth) newLeft = contentWidth - detailWidth;
            }
            return newLeft
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int,
        ) {
            Log.d("SideDragLayout", "onViewPositionChanged: $changedView, $dx")
            //做内容布局移动的时候，详情布局跟着同样的移动
            if (changedView == centerChild) {
                rightChild.layout(rightChild.getLeft() + dx, rightChild.getTop() + dy,
                    rightChild.getRight() + dx, rightChild.getBottom() + dy)
            } else if (changedView == rightChild) {
                //当详情布局移动的时候，内容布局做同样的移动
                centerChild.layout(centerChild.getLeft() + dx, centerChild.getTop() + dy,
                    centerChild.getRight() + dx, centerChild.getBottom() + dy)
            }
            moveDistanceX += dx
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d("SideDragLayout", "onViewReleased: $xvel")
            super.onViewReleased(releasedChild, xvel, yvel)
            if (xvel < -5000) {
                open()
            }else if (xvel > 5000) {
                close()
            }else {
                //松开之后，只要移动超过一半就可以打开或者关闭
                val detailWidth = rightChild.measuredWidth
                if (centerChild.left < -detailWidth / 2) {
                    open()
                } else {
                    close()
                }
            }
        }
    }

    init {
        viewDragHelper = ViewDragHelper.create(this, viewDragCallback)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 3) {
            throw IllegalArgumentException("必须存在3个子View")
        }
        leftChild = getChildAt(0)
        centerChild = getChildAt(1)
        rightChild = getChildAt(2)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount != 3) {
            throw IllegalArgumentException("必须存在3个子View")
        }

        Log.d("SideDragLayout", "onLayout")
        centerChild.layout(moveDistanceX, 0, centerChild.measuredWidth + moveDistanceX, centerChild.measuredHeight)
        leftChild.layout(-leftChild.measuredWidth, 0, 0, leftChild.measuredHeight)
        rightChild.layout(
            centerChild.measuredWidth + moveDistanceX,
            0,
            rightChild.measuredWidth + centerChild.measuredWidth + moveDistanceX,
            rightChild.measuredHeight
        )
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x
                val moveY = event.y
                val dx: Float = moveX - touchX
                val dy: Float = moveY - touchY
                if (Math.abs(dx) > Math.abs(dy)) {
                    requestDisallowInterceptTouchEvent(true)
                }
                touchX = moveX
                touchY = moveY
            }

            MotionEvent.ACTION_UP -> {}
        }
        viewDragHelper.processTouchEvent(event)
        return true
    }


    fun open() {
        val detailWidth = rightChild.measuredWidth
        viewDragHelper.smoothSlideViewTo(centerChild, -detailWidth, 0)
        ViewCompat.postInvalidateOnAnimation(this)
    }


    fun close() {
        viewDragHelper.smoothSlideViewTo(centerChild, 0, 0)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

}